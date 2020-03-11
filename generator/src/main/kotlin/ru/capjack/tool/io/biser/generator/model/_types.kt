package ru.capjack.tool.io.biser.generator.model

import java.util.*

interface Type {
	fun <R, D> accept(visitor: TypeVisitor<R, D>, data: D): R
	
	fun <R> accept(visitor: TypeVisitor<R, Unit>): R = accept(visitor, Unit)
}

enum class PrimitiveType : Type {
	BOOLEAN,
	BYTE,
	INT,
	LONG,
	DOUBLE,
	STRING,
	
	BOOLEAN_ARRAY,
	BYTE_ARRAY,
	INT_ARRAY,
	LONG_ARRAY,
	DOUBLE_ARRAY
	;
	
	override fun <R, D> accept(visitor: TypeVisitor<R, D>, data: D): R {
		return visitor.visitPrimitiveType(this, data)
	}
}

interface StructureType : Type {
	val name: String
	val descriptor: StructureDescriptor
}

class ListType(val element: Type) : Type {
	override fun <R, D> accept(visitor: TypeVisitor<R, D>, data: D): R {
		return visitor.visitListType(this, data)
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as ListType
		if (element != other.element) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("List", element)
	}
}

class NullableType(val original: Type) : Type {
	override fun <R, D> accept(visitor: TypeVisitor<R, D>, data: D): R {
		return visitor.visitNullableType(this, data)
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as NullableType
		if (original != other.original) return false
		return true
	}
	
	override fun hashCode(): Int {
		return Objects.hash("Nullable", original)
	}
}

