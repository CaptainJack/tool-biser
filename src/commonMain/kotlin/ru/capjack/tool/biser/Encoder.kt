package ru.capjack.tool.biser

typealias Encoder<T> = BiserWriter.(T) -> Unit

object Encoders {
	val BOOLEAN: Encoder<Boolean> = BiserWriter::writeBoolean
	val BYTE: Encoder<Byte> = BiserWriter::writeByte
	val INT: Encoder<Int> = BiserWriter::writeInt
	val LONG: Encoder<Long> = BiserWriter::writeLong
	val DOUBLE: Encoder<Double> = BiserWriter::writeDouble
	
	val BOOLEAN_ARRAY: Encoder<BooleanArray> = { writeBooleanArray(it) }
	val BYTE_ARRAY: Encoder<ByteArray> = { writeByteArray(it) }
	val INT_ARRAY: Encoder<IntArray> = { writeIntArray(it) }
	val LONG_ARRAY: Encoder<LongArray> = { writeLongArray(it) }
	val DOUBLE_ARRAY: Encoder<DoubleArray> = { writeDoubleArray(it) }
	
	val STRING: Encoder<String> = BiserWriter::writeString
	val STRING_NULLABLE: Encoder<String?> = BiserWriter::writeStringNullable
	
	fun <E> forList(encoder: Encoder<E>): Encoder<List<E>> = { writeList(it, encoder) }
	
	fun <K, V> forMap(keyEncoder: Encoder<K>, valueEncoder: Encoder<V>): Encoder<Map<K, V>> = { writeMap(it, keyEncoder, valueEncoder) }
}