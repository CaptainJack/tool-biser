package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.ImportsCollection
import ru.capjack.tool.io.biser.generator.model.ListType
import ru.capjack.tool.io.biser.generator.model.NullableType
import ru.capjack.tool.io.biser.generator.model.PrimitiveType
import ru.capjack.tool.io.biser.generator.model.StructureType
import ru.capjack.tool.io.biser.generator.model.TypeVisitor

open class KotlinTypeNameVisitor(protected val targetPackage: CodePath) : TypeVisitor<String, ImportsCollection> {
	override fun visitPrimitiveType(type: PrimitiveType, data: ImportsCollection): String {
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
	
	override fun visitListType(type: ListType, data: ImportsCollection): String {
		return "List<${type.element.accept(this, data)}>"
	}
	
	override fun visitStructureType(type: StructureType, data: ImportsCollection): String {
		data.addImport(targetPackage.resolve(type.path))
		return type.path.name
	}
	
	override fun visitNullableType(type: NullableType, data: ImportsCollection): String {
		return type.original.accept(this, data) + '?'
	}
	
}