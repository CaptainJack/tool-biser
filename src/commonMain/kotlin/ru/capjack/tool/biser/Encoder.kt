package ru.capjack.tool.biser

typealias Encoder<T> = BiserWriter.(T) -> Unit

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
	val STRING_NULLABLE: Encoder<String?> = BiserWriter::writeStringNullable
	
	fun <E> forList(encoder: Encoder<E>): Encoder<List<E>> = { writeList(it, encoder) }
	
	fun <K, V> forMap(keyEncoder: Encoder<K>, valueEncoder: Encoder<V>): Encoder<Map<K, V>> = { writeMap(it, keyEncoder, valueEncoder) }
}