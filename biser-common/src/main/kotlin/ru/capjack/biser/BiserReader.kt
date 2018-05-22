package ru.capjack.biser

import kotlin.reflect.KClass

interface BiserReader {
	fun readBoolean(): Boolean
	
	fun readByte(): Byte
	
	fun readInt(): Int
	
	fun readLong(): Long
	
	fun readDouble(): Double
	
	fun readBytes(): ByteArray
	
	fun readString(): String
	
	fun <E : Enum<E>> readEnum(type: KClass<E>): E
	
	fun <E> readList(decoder: Decoder<E>): List<E>
	
	fun <E> read(decoder: Decoder<E>): E
}