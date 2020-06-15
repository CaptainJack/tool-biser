package ru.capjack.tool.io.biser.generator.model

interface StructureDescriptor {
	val type: StructureType
	
	fun <R, D> accept(visitor: StructureDescriptorVisitor<R, D>, data: D): R
	
	fun <R> accept(visitor: StructureDescriptorVisitor<R, Unit>): R = accept(visitor, Unit)
}

interface EnumDescriptor : StructureDescriptor {
	val lastValueId: Int
	val values: List<EnumValue>
}

class EnumValue(
	val id: Int,
	val name: String
)

interface EntityDescriptor : StructureDescriptor {
	val id: Int
	val fields: List<EntityField>
	val abstract: Boolean
	val children: Set<StructureType>
	val parent: StructureType?
	
	val allChildren: Set<StructureType>
		get() = children + children.flatMap { (it.descriptor as? EntityDescriptor)?.allChildren.orEmpty() }.toSet()
}

interface ObjectDescriptor : StructureDescriptor {
	val id: Int
	val parent: StructureType?
}

class EntityField(
	val name: String,
	val type: Type
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as EntityField
		if (name != other.name) return false
		if (type != other.type) return false
		return true
	}
	
	override fun hashCode(): Int {
		var result = name.hashCode()
		result = 31 * result + type.hashCode()
		return result
	}
}

