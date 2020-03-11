package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

class KotlinTypeNameVisitor : TypeVisitor<String, Unit> {
	override fun visitPrimitiveType(type: PrimitiveType, data: Unit): String {
		return when (type) {
			PrimitiveType.BOOLEAN       -> "Boolean"
			PrimitiveType.BYTE          -> "Byte"
			PrimitiveType.INT           -> "Int"
			PrimitiveType.LONG          -> "Long"
			PrimitiveType.DOUBLE        -> "Double"
			PrimitiveType.STRING        -> "String"
			PrimitiveType.BOOLEAN_ARRAY -> "BooleanArray"
			PrimitiveType.BYTE_ARRAY    -> "ByteArray"
			PrimitiveType.INT_ARRAY     -> "IntArray"
			PrimitiveType.LONG_ARRAY    -> "LongArray"
			PrimitiveType.DOUBLE_ARRAY  -> "DoubleArray"
		}
	}
	
	override fun visitListType(type: ListType, data: Unit): String {
		return "List<${type.element.accept(this, data)}>"
	}
	
	override fun visitStructureType(type: StructureType, data: Unit): String {
		return type.name.substringAfterLast('.')
	}
	
	override fun visitNullableType(type: NullableType, data: Unit): String {
		return type.original.accept(this, data) + '?'
	}
	
}