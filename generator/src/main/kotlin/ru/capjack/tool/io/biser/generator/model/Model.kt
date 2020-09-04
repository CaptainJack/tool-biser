package ru.capjack.tool.io.biser.generator.model

import org.yaml.snakeyaml.Yaml
import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.GeneratorException
import kotlin.math.max

open class Model {
	var change = Change.ABSENT
		private set
	
	val structures: Collection<StructureDescriptor>
		get() = _structures.values
	
	private val _structures = mutableMapOf<String, StructureDescriptorImpl>()
	private var lastEntityId = 0
	
	private val listTypes = mutableMapOf<Type, ListType>()
	private val nullableTypes = mutableMapOf<Type, NullableType>()
	private val structureTypes = mutableMapOf<String, StructureTypeImpl>()
	private val primitiveTypes = PrimitiveType.values().associateBy { it.name }
	
	private val saveTypeVisitor = object : TypeVisitor<String, Unit> {
		override fun visitPrimitiveType(type: PrimitiveType, data: Unit): String {
			return type.name
		}
		
		override fun visitListType(type: ListType, data: Unit): String {
			return type.element.accept(this, data) + '*'
		}
		
		override fun visitStructureType(type: StructureType, data: Unit): String {
			return type.path.value
		}
		
		override fun visitNullableType(type: NullableType, data: Unit): String {
			return type.original.accept(this, data) + '!'
		}
	}
	
	fun raiseChange(to: Change) {
		change = change.raiseTo(to)
	}
	
	fun provideStructureType(name: String): StructureType {
		return structureTypes.getOrPut(name) { StructureTypeImpl(CodePath(name), _structures[name]) }
	}
	
	fun provideListType(type: Type): ListType {
		return listTypes.getOrPut(type) { ListType(type) }
	}
	
	fun provideNullableType(type: Type): NullableType {
		return nullableTypes.getOrPut(type) { NullableType(type) }
	}
	
	fun provideEnumStructure(name: String, values: List<String>): EnumDescriptor {
		oldStructuresNames.remove(name)
		
		val descriptor = _structures[name]
		if (descriptor != null) {
			if (descriptor !is EnumDescriptorImpl) {
				throw GeneratorException(name)
			}
			raiseChange(descriptor.update(values))
			return descriptor
		}
		
		raiseChange(Change.COMPATIBLY)
		
		return EnumDescriptorImpl(provideStructureType(name), values.mapIndexed { i, n -> EnumValue(i + 1, n) }, values.size).also {
			_structures[name] = it
			structureTypes[name]?.descriptor = it
		}
	}
	
	fun provideEntityStructure(name: String, abstract: Boolean, parent: String?, fields: List<EntityField>): EntityDescriptor {
		oldStructuresNames.remove(name)
		
		val descriptor = _structures[name]
		val parentType = parent?.let(::provideStructureType)
		
		if (descriptor != null) {
			if (descriptor !is EntityDescriptorImpl) {
				throw GeneratorException(name)
			}
			raiseChange(descriptor.update(abstract, parentType, fields))
			return descriptor
		}
		
		raiseChange(Change.COMPATIBLY)
		
		return EntityDescriptorImpl(provideStructureType(name), ++lastEntityId, parentType, abstract, fields).also {
			_structures[name] = it
			structureTypes[name]?.descriptor = it
		}
	}
	
	fun provideObjectStructure(name: String, parent: String?): ObjectDescriptor {
		oldStructuresNames.remove(name)
		
		val descriptor = _structures[name]
		val parentType = parent?.let(::provideStructureType)
		
		if (descriptor != null) {
			if (descriptor !is ObjectDescriptorImpl) {
				throw GeneratorException(name)
			}
			raiseChange(descriptor.update(parentType))
			return descriptor
		}
		
		raiseChange(Change.COMPATIBLY)
		
		return ObjectDescriptorImpl(provideStructureType(name), ++lastEntityId, parentType).also {
			_structures[name] = it
			structureTypes[name]?.descriptor = it
		}
	}
	
	private val oldStructuresNames = mutableSetOf<String>()
	
	open fun beginUpdate() {
		oldStructuresNames.addAll(_structures.keys)
	}
	
	open fun completeUpdate() {
		val changed = oldStructuresNames.fold(false) { r, it -> _structures.remove(it) != null || r }
		if (changed) {
			raiseChange(Change.FULL)
		}
		oldStructuresNames.clear()
		
		for (descriptor in _structures.values) {
			if (descriptor is EntityDescriptorImpl) {
				(descriptor.parent?.descriptor as? EntityDescriptorImpl)?.also {
					it.children.add(descriptor.type)
				}
			}
			else if (descriptor is ObjectDescriptorImpl) {
				(descriptor.parent?.descriptor as? EntityDescriptorImpl)?.also {
					it.children.add(descriptor.type)
				}
			}
		}
	}
	
	fun save(): String {
		val data = mutableMapOf<String, Any>()
		save(data)
		return Yaml().dump(data)
	}
	
	fun load(data: String) {
		load(Yaml().load<Map<String, Any>>(data))
		completeUpdate()
	}
	
	protected open fun save(data: MutableMap<String, Any>) {
		
		val structureVisitor = object : StructureDescriptorVisitor<Any, Unit> {
			override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: Unit) = mapOf(
				"name" to descriptor.type.path.value,
				"lastValueId" to descriptor.lastValueId,
				"values" to descriptor.values.map {
					mapOf(
						"id" to it.id,
						"name" to it.name
					)
				}
			)
			
			override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: Unit) = mapOf(
				"name" to descriptor.type.path.value,
				"id" to descriptor.id,
				"parent" to descriptor.parent?.path?.value,
				"abstract" to descriptor.abstract,
				"fields" to descriptor.fields.map {
					mapOf(
						"name" to it.name,
						"type" to saveType(it.type)
					)
				}
			)
			
			override fun visitObjectStructureDescriptor(descriptor: ObjectDescriptor, data: Unit) = mapOf(
				"name" to descriptor.type.path.value,
				"id" to descriptor.id,
				"parent" to descriptor.parent?.path?.value
			)
		}
		
		data["lastEntityId"] = lastEntityId
		data["structures"] = _structures.values.map { it.accept(structureVisitor) }
	}
	
	@Suppress("UNCHECKED_CAST")
	protected open fun load(data: Map<String, Any>) {
		lastEntityId = data["lastEntityId"] as Int
		var maxEntityId = 0
		
		data["structures"].asObjectList().associateTo(_structures) { s ->
			val name = s["name"] as String
			Pair(name,
				(
					if (s.containsKey("id")) {
						val id = s["id"] as Int
						maxEntityId = max(id, maxEntityId)
						if (s.containsKey("fields")) {
							EntityDescriptorImpl(
								provideStructureType(name),
								id,
								(s["parent"] as String?)?.let { provideStructureType(it) },
								s["abstract"] as Boolean,
								s["fields"].asObjectList().map { f ->
									EntityField(
										f["name"] as String,
										loadType(f["type"] as String)
									)
								}
							)
						}
						else {
							ObjectDescriptorImpl(
								provideStructureType(name),
								id,
								(s["parent"] as String?)?.let { provideStructureType(it) }
							)
						}
					}
					else
						EnumDescriptorImpl(
							provideStructureType(name),
							s["values"].asObjectList().map {
								EnumValue(
									it["id"] as Int,
									it["name"] as String
								)
							},
							s["lastValueId"] as Int
						)
					).also { structureTypes[name]?.descriptor = it }
			)
		}
		
		lastEntityId = max(maxEntityId, lastEntityId)
	}
	
	protected fun loadType(name: String): Type {
		return when {
			name.endsWith('*') -> provideListType(loadType(name.dropLast(1)))
			name.endsWith('!') -> provideNullableType(loadType(name.dropLast(1)))
			else               -> primitiveTypes[name] ?: provideStructureType(name)
		}
	}
	
	protected fun saveType(type: Type): String {
		return type.accept(saveTypeVisitor)
	}
	
	protected fun Any?.asObjectList(): List<Map<String, Any>> {
		@Suppress("UNCHECKED_CAST")
		return this as List<Map<String, Any>>
	}
	
}