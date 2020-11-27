package ru.capjack.tool.biser

typealias Encoder<T> = BiserWriter.(T) -> Unit

interface BiserWriter {
	fun writeBoolean(value: Boolean)
	
	fun writeByte(value: Byte)
	
	fun writeInt(value: Int)
	
	fun writeLong(value: Long)
	
	fun writeDouble(value: Double)
	
	
	fun writeBooleanArray(value: BooleanArray)
	
	fun writeByteArray(value: ByteArray)
	
	fun writeIntArray(value: IntArray)
	
	fun writeLongArray(value: LongArray)
	
	fun writeDoubleArray(value: DoubleArray)
	
	
	fun writeString(value: String)
	
	fun writeStringNullable(value: String?)
	
	fun <E> writeList(value: List<E>, encoder: Encoder<E>)
	
	fun <T> write(value: T, encoder: Encoder<T>)
}