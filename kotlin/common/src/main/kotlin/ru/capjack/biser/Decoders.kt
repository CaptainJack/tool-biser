package ru.capjack.biser

import kotlin.reflect.KClass

object Decoders {
	val BYTE: Decoder<Byte> = BiserReader::readByte
	val BOOLEAN: Decoder<Boolean> = BiserReader::readBoolean
	val INT: Decoder<Int> = BiserReader::readInt
	val LONG: Decoder<Long> = BiserReader::readLong
	val DOUBLE: Decoder<Double> = BiserReader::readDouble
	val BYTES: Decoder<ByteArray> = BiserReader::readBytes
	val STRING: Decoder<String> = BiserReader::readString
	
	fun <T : Enum<T>> enum(type: KClass<T>): Decoder<T> {
		return { it.readEnum(type) }
	}
	
	fun <T : Any> list(decoder: Decoder<T>): Decoder<List<T>> {
		return { it.readList(decoder) }
	}
	
	inline fun <reified T : Enum<T>> enum(): Decoder<T> {
		return enum(T::class)
	}
}