package ru.capjack.tool.biser.generator.kotlin

import ru.capjack.tool.biser.generator.CoderNameScopeVisitor
import ru.capjack.tool.biser.generator.model.EntityDescriptor
import ru.capjack.tool.biser.generator.model.EnumDescriptor
import ru.capjack.tool.biser.generator.model.ListType
import ru.capjack.tool.biser.generator.model.NullableType
import ru.capjack.tool.biser.generator.model.ObjectDescriptor
import ru.capjack.tool.biser.generator.model.PrimitiveType
import ru.capjack.tool.biser.generator.model.StructureDescriptorVisitor
import ru.capjack.tool.biser.generator.model.StructureType
import ru.capjack.tool.biser.generator.model.TypeVisitor

open class KotlinCoderNameVisitor(val scope: CoderNameScopeVisitor) : TypeVisitor<String, Unit>, StructureDescriptorVisitor<String, String> {
	private var deep = 0
	
	override fun visitPrimitiveType(type: PrimitiveType, data: Unit): String {
		val name = when (type) {
			PrimitiveType.BOOLEAN       -> "BOOLEAN"
			PrimitiveType.BYTE          -> "BYTE"
			PrimitiveType.INT           -> "INT"
			PrimitiveType.LONG          -> "LONG"
			PrimitiveType.DOUBLE        -> "DOUBLE"
			PrimitiveType.STRING        -> "STRING"
			PrimitiveType.BOOLEAN_ARRAY -> "BOOLEAN_ARRAY"
			PrimitiveType.BYTE_ARRAY    -> "BYTE_ARRAY"
			PrimitiveType.INT_ARRAY     -> "INT_ARRAY"
			PrimitiveType.LONG_ARRAY    -> "LONG_ARRAY"
			PrimitiveType.DOUBLE_ARRAY  -> "DOUBLE_ARRAY"
		}
		return if (deep == 0) scope.visitPrimitiveScope(name) else name
	}
	
	override fun visitListType(type: ListType, data: Unit): String {
		++deep
		val name = "LIST_" + type.element.accept(this, data)
		--deep
		return if (deep == 0) scope.visitGeneratedScope(name) else name
	}
	
	override fun visitStructureType(type: StructureType, data: Unit): String {
		val name = type.descriptor.accept(this, type.path.asString('_'))
		return if (deep == 0) scope.visitGeneratedScope(name) else name
	}
	
	override fun visitNullableType(type: NullableType, data: Unit): String {
		++deep
		val name = "NULLABLE_" + type.original.accept(this, data)
		--deep
		return if (deep == 0) scope.visitGeneratedScope(name) else name
	}
	
	override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: String): String {
		return "ENUM_$data"
	}
	
	override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: String): String {
		return "ENTITY_$data"
	}
	
	override fun visitObjectStructureDescriptor(descriptor: ObjectDescriptor, data: String): String {
		return "OBJECT_$data"
	}
}

