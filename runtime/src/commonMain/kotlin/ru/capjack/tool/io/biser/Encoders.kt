package ru.capjack.tool.io.biser

object Encoders {
	val BOOLEAN: Encoder<Boolean> = BiserWriter::writeBoolean
	val BYTE: Encoder<Byte> = BiserWriter::writeByte
	val INT: Encoder<Int> = BiserWriter::writeInt
	val LONG: Encoder<Long> = BiserWriter::writeLong
	val DOUBLE: Encoder<Double> = BiserWriter::writeDouble
	
	val BOOLEAN_ARRAY: Encoder<BooleanArray> = BiserWriter::writeBooleanArray
	val BYTE_ARRAY: Encoder<ByteArray> = BiserWriter::writeByteArray
	val INT_ARRAY: Encoder<IntArray> = BiserWriter::writeIntArray
	val LONG_ARRAY: Encoder<LongArray> = BiserWriter::writeLongArray
	val DOUBLE_ARRAY: Encoder<DoubleArray> = BiserWriter::writeDoubleArray
	
	val STRING: Encoder<String> = BiserWriter::writeString
	
	fun <E> ofList(encoder: Encoder<E>): Encoder<List<E>> = { writeList(it, encoder) }
}