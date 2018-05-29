package ru.capjack.biser

interface BiserWriter {
	fun writeBoolean(value: Boolean)
	
	fun writeByte(value: Byte)
	
	fun writeInt(value: Int)
	
	fun writeLong(value: Long)
	
	fun writeDouble(value: Double)
	
	fun writeBytes(value: ByteArray)
	
	fun writeString(value: String)
	
	fun writeEnum(value: Enum<*>)
	
	fun <E> writeList(value: List<E>, encoder: Encoder<E>)
	
	fun <T> write(value: T, encoder: Encoder<T>)
}

