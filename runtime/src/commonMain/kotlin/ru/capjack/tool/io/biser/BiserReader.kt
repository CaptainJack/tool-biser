package ru.capjack.tool.io.biser

typealias Decoder<T> = BiserReader.() -> T

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
	
	fun <E> readList(decoder: Decoder<E>): List<E>
	
	fun <E> read(decoder: Decoder<E>): E
}