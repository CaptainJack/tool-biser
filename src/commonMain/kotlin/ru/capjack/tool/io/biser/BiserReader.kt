package ru.capjack.tool.io.biser

import kotlin.reflect.KClass

interface BiserReader {
	fun readBoolean(): Boolean
	
	fun readByte(): Byte
	
	fun readShort(): Short
	
	fun readInt(): Int
	
	fun readLong(): Long
	
	fun readDouble(): Double
	
	
	fun readBooleanArray(): BooleanArray
	
	fun readByteArray(): ByteArray
	
	fun readShortArray(): ShortArray
	
	fun readIntArray(): IntArray
	
	fun readLongArray(): LongArray
	
	fun readDoubleArray(): DoubleArray
	
	
	fun readString(): String?
	
	fun <E : Enum<E>> readEnum(type: KClass<E>): E
	
	fun <E> readList(decoder: Decoder<E>): List<E>
	
	fun <E> read(decoder: Decoder<E>): E
}