package ru.capjack.biser


object Encoders {
	val BOOLEAN: Encoder<Boolean> = BiserWriter::writeBoolean
	val BYTE: Encoder<Byte> = BiserWriter::writeByte
	val INT: Encoder<Int> = BiserWriter::writeInt
	val LONG: Encoder<Long> = BiserWriter::writeLong
	val DOUBLE: Encoder<Double> = BiserWriter::writeDouble
	val BYTES: Encoder<ByteArray> = BiserWriter::writeBytes
	val STRING: Encoder<String> = BiserWriter::writeString
	val ENUM: Encoder<Enum<*>> = BiserWriter::writeEnum
	
	fun <E : Any> list(encoder: Encoder<E>): Encoder<List<E>> {
		return { writer, value -> writer.writeList(value, encoder) }
	}
}