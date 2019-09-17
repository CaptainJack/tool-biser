package ru.capjack.tool.io.biser

interface BiserWriter {
	fun writeBoolean(value: Boolean)
	
	fun writeByte(value: Byte)
	
	fun writeShort(value: Short)
	
	fun writeInt(value: Int)
	
	fun writeLong(value: Long)
	
	fun writeDouble(value: Double)
	
	
	fun writeBooleanArray(value: BooleanArray)
	
	fun writeByteArray(value: ByteArray)
	
	fun writeShortArray(value: ShortArray)
	
	fun writeIntArray(value: IntArray)
	
	fun writeLongArray(value: LongArray)
	
	fun writeDoubleArray(value: DoubleArray)
	
	
	fun writeString(value: String?)
	
	fun writeEnum(value: Enum<*>)
	
	fun <E> writeList(value: List<E>, encoder: Encoder<E>)
	
	fun <T> write(value: T, encoder: Encoder<T>)
}