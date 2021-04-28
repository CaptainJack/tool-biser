package ru.capjack.tool.biser

import ru.capjack.tool.io.ArrayByteBuffer
import ru.capjack.tool.io.readArray
import kotlin.test.Test

class TestByteBufferBiserWriter {
	@Test
	fun writeBoolean() {
		write(Encoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun writeByte() {
		write(Encoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun writeInt() {
		write(Encoders.INT, DataPairs.int)
	}
	
	@Test
	fun writeLong() {
		write(Encoders.LONG, DataPairs.long)
	}
	
	@Test
	fun writeDouble() {
		write(Encoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun writeBooleanArray() {
		write(Encoders.BOOLEAN_ARRAY, DataPairs.booleanArray)
	}
	
	@Test
	fun writeByteArray() {
		write(Encoders.BYTE_ARRAY, DataPairs.byteArray)
	}
	
	@Test
	fun writeIntArray() {
		write(Encoders.INT_ARRAY, DataPairs.intArray)
	}
	
	@Test
	fun writeLongArray() {
		write(Encoders.LONG_ARRAY, DataPairs.longArray)
	}
	
	@Test
	fun writeDoubleArray() {
		write(Encoders.DOUBLE_ARRAY, DataPairs.doubleArray)
	}
	
	@Test
	fun writeString() {
		write(Encoders.STRING, DataPairs.string)
	}
	
	@Test
	fun writeStringNullable() {
		write(Encoders.STRING_NULLABLE, DataPairs.stringNullable)
	}
	
	@Test
	fun writeList() {
		write(
			Encoders.forList(Encoders.STRING),
			DataPairs.listString
		)
	}
	
	@Test
	fun writeListList() {
		write(
			Encoders.forList(Encoders.forList(Encoders.INT)),
			DataPairs.listListInt
		)
	}
	
	@Test
	fun writeListArray() {
		write(
			Encoders.forList(Encoders.INT_ARRAY),
			DataPairs.listIntArray
		)
	}
	
	@Test
	fun writeMap() {
		write(
			Encoders.forMap(Encoders.INT, Encoders.STRING),
			DataPairs.map
		)
	}
	
	private fun <T> write(encoder: Encoder<T>, pairs: List<Pair<T, ByteArray>>) {
		val buffer = ArrayByteBuffer()
		val writer = ByteBufferBiserWriter(buffer)
		
		for (pair in pairs) {
			buffer.clear()
			writer.write(pair.first, encoder)
			assertPrimitiveArrayEquals(pair.second, buffer.readArray())
		}
	}
	
}