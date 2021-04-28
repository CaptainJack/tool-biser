package ru.capjack.tool.biser

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
	
	fun <E> writeList(value: Collection<E>, encoder: Encoder<E>)
	
	fun <K, V> writeMap(value: Map<K, V>, keyEncoder: Encoder<K>, valueEncoder: Encoder<V>)
	
	fun <T> write(value: T, encoder: Encoder<T>)
}