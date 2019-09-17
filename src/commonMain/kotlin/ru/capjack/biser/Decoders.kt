package ru.capjack.biser

import kotlin.reflect.KClass

object Decoders {
	val BOOLEAN: Decoder<Boolean> = BiserReader::readBoolean
	val BYTE: Decoder<Byte> = BiserReader::readByte
	val SHORT: Decoder<Short> = BiserReader::readShort
	val INT: Decoder<Int> = BiserReader::readInt
	val LONG: Decoder<Long> = BiserReader::readLong
	val DOUBLE: Decoder<Double> = BiserReader::readDouble
	
	val BOOLEAN_ARRAY: Decoder<BooleanArray> = BiserReader::readBooleanArray
	val BYTE_ARRAY: Decoder<ByteArray> = BiserReader::readByteArray
	val SHORT_ARRAY: Decoder<ShortArray> = BiserReader::readShortArray
	val INT_ARRAY: Decoder<IntArray> = BiserReader::readIntArray
	val LONG_ARRAY: Decoder<LongArray> = BiserReader::readLongArray
	val DOUBLE_ARRAY: Decoder<DoubleArray> = BiserReader::readDoubleArray
	
	val STRING: Decoder<String?> = BiserReader::readString
	
	fun <T : Enum<T>> ofEnum(type: KClass<T>): Decoder<T> = { it.readEnum(type) }
	
	inline fun <reified T : Enum<T>> ofEnum(): Decoder<T> = ofEnum(T::class)
	
	fun <T> ofList(decoder: Decoder<T>): Decoder<List<T>> = { it.readList(decoder) }
}
