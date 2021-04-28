package ru.capjack.tool.biser

typealias Decoder<T> = BiserReader.() -> T

object Decoders {
	val BOOLEAN: Decoder<Boolean> = BiserReader::readBoolean
	val BYTE: Decoder<Byte> = BiserReader::readByte
	val INT: Decoder<Int> = BiserReader::readInt
	val LONG: Decoder<Long> = BiserReader::readLong
	val DOUBLE: Decoder<Double> = BiserReader::readDouble
	
	val BOOLEAN_ARRAY: Decoder<BooleanArray> = BiserReader::readBooleanArray
	val BYTE_ARRAY: Decoder<ByteArray> = BiserReader::readByteArray
	val INT_ARRAY: Decoder<IntArray> = BiserReader::readIntArray
	val LONG_ARRAY: Decoder<LongArray> = BiserReader::readLongArray
	val DOUBLE_ARRAY: Decoder<DoubleArray> = BiserReader::readDoubleArray
	
	val STRING: Decoder<String> = BiserReader::readString
	val STRING_NULLABLE: Decoder<String?> = BiserReader::readStringNullable
	
	fun <E> forList(decoder: Decoder<E>): Decoder<List<E>> = { readList(decoder) }
	
	fun <K, V> forMap(keyDecoder: Decoder<K>, valueDecoder: Decoder<V>): Decoder<Map<K, V>> = { readMap(keyDecoder, valueDecoder) }
}
