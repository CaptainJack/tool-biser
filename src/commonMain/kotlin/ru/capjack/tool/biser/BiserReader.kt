package ru.capjack.tool.biser

interface BiserReader {
	fun readBoolean(): Boolean
	
	fun readByte(): Byte
	
	fun readInt(): Int
	
	fun readLong(): Long
	
	fun readDouble(): Double
	
	
	fun readBooleanArray(): BooleanArray
	
	fun readByteArray(): ByteArray
	
	fun readIntArray(): IntArray
	
	fun readLongArray(): LongArray
	
	fun readDoubleArray(): DoubleArray
	
	
	fun readString(): String
	
	fun readStringNullable(): String?
	
	fun <E> readList(decoder: Decoder<E>): List<E>
	
	fun <K, V> readMap(keyDecoder: Decoder<K>, valueDecoder: Decoder<V>): Map<K, V>
	
	fun <E> read(decoder: Decoder<E>): E
}