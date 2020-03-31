package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

class KotlinWriteCallVisitor(private val names: TypeVisitor<String, Unit>) : TypeVisitor<String, String> {
	
	override fun visitPrimitiveType(type: PrimitiveType, data: String): String {
		return when (type) {
			PrimitiveType.BOOLEAN       -> "writeBoolean($data)"
			PrimitiveType.BYTE          -> "writeByte($data)"
			PrimitiveType.INT           -> "writeInt($data)"
			PrimitiveType.LONG          -> "writeLong($data)"
			PrimitiveType.DOUBLE        -> "writeDouble($data)"
			PrimitiveType.STRING        -> "writeString($data)"
			PrimitiveType.BOOLEAN_ARRAY -> "writeBooleanArray($data)"
			PrimitiveType.BYTE_ARRAY    -> "writeByteArray($data)"
			PrimitiveType.INT_ARRAY     -> "writeIntArray($data)"
			PrimitiveType.LONG_ARRAY    -> "writeLongArray($data)"
			PrimitiveType.DOUBLE_ARRAY  -> "writeDoubleArray($data)"
		}
	}
	
	override fun visitListType(type: ListType, data: String): String {
		return "writeList($data, ${type.element.accept(names)})"
	}
	
	override fun visitStructureType(type: StructureType, data: String): String {
		return "write($data, ${type.accept(names)})"
	}
	
	override fun visitNullableType(type: NullableType, data: String): String {
		return "write($data, ${type.accept(names)})"
	}
}