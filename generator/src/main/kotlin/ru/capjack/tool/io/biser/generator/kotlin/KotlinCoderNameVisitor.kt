package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.EntityDescriptor
import ru.capjack.tool.io.biser.generator.model.EnumDescriptor
import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureDescriptorVisitor
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

open class KotlinCoderNameVisitor(encoder: Boolean) : TypeVisitor<String, Unit>, StructureDescriptorVisitor<String, String> {
	private var deep = 0
	private val primitiveName = if (encoder) "Encoders" else "Decoders"
	
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
		return if (deep == 0) "$primitiveName.$name" else name
	}
	
	override fun visitListType(type: ListType, data: Unit): String {
		++deep
		val name = "LIST_" + type.element.accept(this)
		--deep
		return name
	}
	
	override fun visitStructureType(type: StructureType, data: Unit): String {
		return type.descriptor.accept(this, type.name.replace(".", "_"))
	}
	
	override fun visitNullableType(type: NullableType, data: Unit): String {
		++deep
		val name = "NULLABLE_" + type.original.accept(this)
		--deep
		return name
	}
	
	override fun visitEnumStructureDescriptor(descriptor: EnumDescriptor, data: String): String {
		return "ENUM_$data"
	}
	
	override fun visitEntityStructureDescriptor(descriptor: EntityDescriptor, data: String): String {
		return "ENTITY_$data"
	}
}