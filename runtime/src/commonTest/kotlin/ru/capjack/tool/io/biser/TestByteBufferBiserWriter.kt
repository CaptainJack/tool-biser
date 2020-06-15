package ru.capjack.tool.io.biser

import ru.capjack.tool.io.ArrayByteBuffer
import ru.capjack.tool.io.readToArray
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore //TODO Legacy
class TestByteBufferBiserWriter {
	@Test
	fun testWriteBoolean() {
		testWrite(Encoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun testWriteByte() {
		testWrite(Encoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun testWriteInt() {
		testWrite(Encoders.INT, DataPairs.int)
	}
	
	@Test
	fun testWriteLong() {
		testWrite(Encoders.LONG, DataPairs.long)
	}
	
	@Test
	fun testWriteDouble() {
		testWrite(Encoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun testWriteBooleanArray() {
		testWrite(Encoders.BOOLEAN_ARRAY, DataPairs.booleanArray)
	}
	
	@Test
	fun testWriteByteArray() {
		testWrite(Encoders.BYTE_ARRAY, DataPairs.byteArray)
	}
	
	@Test
	fun testWriteIntArray() {
		testWrite(Encoders.INT_ARRAY, DataPairs.intArray)
	}
	
	@Test
	fun testWriteLongArray() {
		testWrite(Encoders.LONG_ARRAY, DataPairs.longArray)
	}
	
	@Test
	fun testWriteDoubleArray() {
		testWrite(Encoders.DOUBLE_ARRAY, DataPairs.doubleArray)
	}
	
	@Test
	fun testWriteString() {
		testWrite(Encoders.STRING, DataPairs.string)
	}
	
	@Test
	fun testWriteList() {
		testWrite(
			Encoders.ofList(Encoders.STRING),
			DataPairs.listString
		)
	}
	
	@Test
	fun testWriteListList() {
		testWrite(
			Encoders.ofList(Encoders.ofList(Encoders.INT)),
			DataPairs.listListInt
		)
	}
	
	@Test
	fun testWriteListArray() {
		testWrite(
			Encoders.ofList(Encoders.INT_ARRAY),
			DataPairs.listIntArray
		)
	}
	
	private fun <T> testWrite(encoder: Encoder<T>, pairs: List<Pair<T, ByteArray>>) {
		val buffer = ArrayByteBuffer()
		val writer = ByteBufferBiserWriter(buffer)
		
		for (pair in pairs) {
			buffer.clear()
			writer.write(pair.first, encoder)
			assertPrimitiveArrayEquals(pair.second, buffer.readToArray())
		}
	}
	
}