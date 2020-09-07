package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

class KotlinReadCallVisitor(private val names: TypeVisitor<String, Unit>) : TypeVisitor<String, Unit> {
	
	override fun visitPrimitiveType(type: PrimitiveType, data: Unit): String {
		return when (type) {
			PrimitiveType.BOOLEAN       -> "readBoolean()"
			PrimitiveType.BYTE          -> "readByte()"
			PrimitiveType.INT           -> "readInt()"
			PrimitiveType.LONG          -> "readLong()"
			PrimitiveType.DOUBLE        -> "readDouble()"
			PrimitiveType.STRING        -> "readString()"
			PrimitiveType.BOOLEAN_ARRAY -> "readBooleanArray()"
			PrimitiveType.BYTE_ARRAY    -> "readByteArray()"
			PrimitiveType.INT_ARRAY     -> "readIntArray()"
			PrimitiveType.LONG_ARRAY    -> "readLongArray()"
			PrimitiveType.DOUBLE_ARRAY  -> "readDoubleArray()"
		}
	}
	
	override fun visitListType(type: ListType, data: Unit): String {
		return "readList(${type.element.accept(names)})"
	}
	
	override fun visitStructureType(type: StructureType, data: Unit): String {
		return "read(${type.accept(names)})"
	}
	
	override fun visitNullableType(type: NullableType, data: Unit): String {
		if (type.original == PrimitiveType.STRING) {
			return "readStringNullable()"
		}
		return "read(${type.accept(names)})"
	}
	
}