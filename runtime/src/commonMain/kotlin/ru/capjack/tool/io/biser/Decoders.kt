package ru.capjack.tool.io.biser

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
	
	fun <T> ofList(decoder: Decoder<T>): Decoder<List<T>> = { readList(decoder) }
}
