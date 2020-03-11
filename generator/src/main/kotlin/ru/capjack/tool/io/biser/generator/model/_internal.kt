package ru.capjack.tool.io.biser.generator.model

import java.util.*

internal class InternalStructureType(override val name: String, descriptor: StructureDescriptor?) : StructureType {
	override lateinit var descriptor: StructureDescriptor
	
	init {
		if (descriptor != null) {
			this.descriptor = descriptor
		}
	}
	
	override fun <R, D> accept(visitor: TypeVisitor<R, D>, data: D): R {
		return visitor.visitStructureType(this, data)
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as StructureType
		if (name != other.name) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("Structure", name)
	}
}

internal abstract class InternalStructureDescriptor(override val type: StructureType) : StructureDescriptor {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is InternalStructureDescriptor) return false
		if (type != other.type) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("StructureDescriptor", type)
	}
}

internal class InternalEnumDescriptor(
	type: StructureType,
	values: List<EnumValue>,
	lastValueId: Int = 0
) : InternalStructureDescriptor(type), EnumDescriptor {
	override val values = values.toMutableList()
	
	override var lastValueId = lastValueId
		private set
	
	override fun <R, D> accept(visitor: StructureDescriptorVisitor<R, D>, data: D): R {
		return visitor.visitEnumStructureDescriptor(this, data)
	}
	
	fun update(values: List<String>): Change {
		var change = Change.ABSENT
		
		if (this.values.retainAll { it.name in values }) {
			change = Change.FULL
		}
		
		values.forEach { v ->
			if (!this.values.any { it.name == v }) {
				this.values.add(EnumValue(++lastValueId, v))
				change = change.raiseTo(Change.COMPATIBLY)
			}
		}
		
		return change
	}
}

internal class InternalEntityDescriptor(
	type: StructureType,
	override var id: Int,
	override var parent: StructureType?,
	override var abstract: Boolean,
	fields: List<EntityField>
) : InternalStructureDescriptor(type), EntityDescriptor {
	
	override val children = mutableSetOf<StructureType>()
	
	override val fields = fields.toMutableList()
	
	override fun <R, D> accept(visitor: StructureDescriptorVisitor<R, D>, data: D): R {
		return visitor.visitEntityStructureDescriptor(this, data)
	}
	
	fun update(abstract: Boolean, parent: StructureType?, fields: List<EntityField>): Change {
		var change = Change.ABSENT
		
		if (this.parent != parent) {
			this.parent = parent
			change = Change.FULL
		}
		
		if (this.abstract != abstract) {
			this.abstract = abstract
			change = Change.FULL
		}
		
		if (this.fields != fields) {
			this.fields.clear()
			this.fields.addAll(fields)
			change = Change.FULL
		}
		
		return change
	}
	
}

