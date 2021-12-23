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
	
	
	fun readBooleanArray(target: BooleanArray, offset: Int = 0): Int
	
	fun readByteArray(target: ByteArray, offset: Int = 0): Int
	
	fun readIntArray(target: IntArray, offset: Int = 0): Int
	
	fun readLongArray(target: LongArray, offset: Int = 0): Int
	
	fun readDoubleArray(target: DoubleArray, offset: Int = 0): Int
	
	
	fun readString(): String
	
	fun readStringNullable(): String?
	
	fun <E> readList(decoder: Decoder<E>): List<E>
	
	fun <E> readList(target: MutableCollection<E>, decoder: Decoder<E>): Int
	
	fun <K, V> readMap(keyDecoder: Decoder<K>, valueDecoder: Decoder<V>): Map<K, V>
	
	fun <K, V> readMap(target: MutableMap<K, V>, keyDecoder: Decoder<K>, valueDecoder: Decoder<V>): Int
	
	fun <E> read(decoder: Decoder<E>): E
	
	fun readIterate(decoder: Decoder<Unit>): Int
}