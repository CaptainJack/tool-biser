package ru.capjack.tool.biser.generator.model

import ru.capjack.tool.biser.generator.CodePath
import java.util.*

internal class StructureTypeImpl(override val path: CodePath, descriptor: StructureDescriptor?) : StructureType {
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
		if (path != other.path) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("Structure", path)
	}
}

internal abstract class StructureDescriptorImpl(override val type: StructureType) : StructureDescriptor {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is StructureDescriptorImpl) return false
		if (type != other.type) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("StructureDescriptor", type)
	}
}

internal class EnumDescriptorImpl(
	type: StructureType,
	values: List<EnumValue>,
	lastValueId: Int = 0
) : StructureDescriptorImpl(type), EnumDescriptor {
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

internal class EntityDescriptorImpl(
	type: StructureType,
	override var id: Int,
	override var parent: StructureType?,
	override var abstract: Boolean,
	fields: List<EntityField>
) : StructureDescriptorImpl(type), EntityDescriptor {
	
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


internal class ObjectDescriptorImpl(
	type: StructureType,
	override var id: Int,
	override var parent: StructureType?
) : StructureDescriptorImpl(type), ObjectDescriptor {
	
	override fun <R, D> accept(visitor: StructureDescriptorVisitor<R, D>, data: D): R {
		return visitor.visitObjectStructureDescriptor(this, data)
	}
	
	fun update(parent: StructureType?): Change {
		var change = Change.ABSENT
		
		if (this.parent != parent) {
			this.parent = parent
			change = Change.FULL
		}
		
		return change
	}
	
}

