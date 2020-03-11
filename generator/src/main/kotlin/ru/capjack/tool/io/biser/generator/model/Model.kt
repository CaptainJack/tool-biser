package ru.capjack.tool.io.biser.generator.model

import org.yaml.snakeyaml.Yaml
import ru.capjack.tool.io.biser.generator.GeneratorException

open class Model {
	var change = Change.ABSENT
		private set
	
	val structures: Collection<StructureDescriptor>
		get() = _structures.values
	
	private val _structures = mutableMapOf<String, InternalStructureDescriptor>()
	private var lastEntityId = 0
	
	private val listTypes = mutableMapOf<Type, ListType>()
	private val nullableTypes = mutableMapOf<Type, NullableType>()
	private val structureTypes = mutableMapOf<String, InternalStructureType>()
	
	fun provideStructureType(name: String): StructureType {
		return structureTypes.getOrPut(name) { InternalStructureType(name, _structures[name]) }
	}
	
	fun provideListType(type: Type): ListType {
		return listTypes.getOrPut(type) { ListType(type) }
	}
	
	fun provideNullableType(type: Type): NullableType {
		return nullableTypes.getOrPut(type) { NullableType(type) }
	}
	
	fun provideEnumStructure(name: String, values: List<String>): EnumDescriptor {
		val descriptor = _structures[name]
		if (descriptor != null) {
			if (descriptor !is InternalEnumDescriptor) {
				throw GeneratorException(name)
			}
			updateChange(descriptor.update(values))
			return descriptor
		}
		
		updateChange(Change.COMPATIBLY)
		
		return InternalEnumDescriptor(provideStructureType(name), values.mapIndexed { i, n -> EnumValue(i + 1, n) }, values.size).also {
			_structures[name] = it
			structureTypes[name]?.descriptor = it
		}
	}
	
	fun provideEntityStructure(name: String, abstract: Boolean, parent: String?, fields: List<EntityField>): EntityDescriptor {
		val descriptor = _structures[name]
		val parentType = parent?.let(::provideStructureType)
		
		if (descriptor != null) {
			if (descriptor !is InternalEntityDescriptor) {
				throw GeneratorException(name)
			}
			updateChange(descriptor.update(abstract, parentType, fields))
			return descriptor
		}
		
		updateChange(Change.COMPATIBLY)
		
		return InternalEntityDescriptor(provideStructureType(name), ++lastEntityId, parentType, abstract, fields).also {
			_structures[name] = it
			structureTypes[name]?.descriptor = it
		}
	}
	
	fun complete() {
		for (descriptor in _structures.values) {
			if (descriptor is InternalEntityDescriptor) {
				(descriptor.parent?.descriptor as? InternalEntityDescriptor)?.also {
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
		complete()
	}
	
	fun updateChange(change: Change) {
		this.change = this.change.raiseTo(change)
	}
	
	protected open fun save(data: MutableMap<String, Any>) {
		val typeVisitor = object : TypeVisitor<String, Unit> {
			override fun visitPrimitiveType(type: PrimitiveType, data: Unit): String {
				return type.name
			}
			
			override fun visitListType(type: ListType, data: Unit): String {
				return type.element.accept(this, data) + '*'
			}
			
			override fun visitStructureType(type: StructureType, data: Unit): String {
				return type.name
			}
			
			override fun visitNullableType(type: NullableType, data: Unit): String {
				return type.original.accept(this, data) + '!'
			}
		}
		
		val structureVisitor = object : StructureDescriptorVisitor<Any, Unit> {
			override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: Unit) = mapOf(
				"name" to descriptor.type.name,
				"lastValueId" to descriptor.lastValueId,
				"values" to descriptor.values.map {
					mapOf(
						"id" to it.id,
						"name" to it.name
					)
				}
			)
			
			override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: Unit) = mapOf(
				"name" to descriptor.type.name,
				"id" to descriptor.id,
				"parent" to descriptor.parent?.name,
				"abstract" to descriptor.abstract,
				"fields" to descriptor.fields.map {
					mapOf(
						"name" to it.name,
						"type" to it.type.accept(typeVisitor)
					)
				}
			)
		}
		
		data["lastEntityId"] = lastEntityId
		data["structures"] = _structures.values.map { it.accept(structureVisitor) }
	}
	
	
	@Suppress("UNCHECKED_CAST")
	protected open fun load(data: Map<String, Any>) {
		lastEntityId = data["lastEntityId"] as Int
		
		val primitiveTypes = PrimitiveType.values().associateBy { it.name }
		
		fun provideType(name: String): Type {
			return when {
				name.endsWith('*') -> provideListType(provideType(name.dropLast(1)))
				name.endsWith('!') -> provideNullableType(provideType(name.dropLast(1)))
				else               -> primitiveTypes[name] ?: provideStructureType(name)
			}
		}
		
		data["structures"].asObjectList().associateTo(_structures) { s ->
			val name = s["name"] as String
			Pair(name,
				(
					if (s.containsKey("id"))
						InternalEntityDescriptor(
							provideStructureType(name),
							s["id"] as Int,
							(s["parent"] as String?)?.let { provideStructureType(it) },
							s["abstract"] as Boolean,
							s["fields"].asObjectList().map { f ->
								EntityField(
									f["name"] as String,
									provideType(f["type"] as String)
								)
							}
						)
					else
						InternalEnumDescriptor(
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
	}
	
	private fun Any?.asObjectList(): List<Map<String, Any>> {
		@Suppress("UNCHECKED_CAST")
		return this as List<Map<String, Any>>
	}
	
}