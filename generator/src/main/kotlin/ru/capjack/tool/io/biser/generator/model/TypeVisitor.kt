package ru.capjack.tool.io.biser.generator.model

interface TypeVisitor<R, D> {
	fun visitPrimitiveType(type: PrimitiveType, data: D): R
	
	fun visitListType(type: ListType, data: D): R
	
	fun visitStructureType(type: StructureType, data: D): R
	
	fun visitNullableType(type: NullableType, data: D): R
}