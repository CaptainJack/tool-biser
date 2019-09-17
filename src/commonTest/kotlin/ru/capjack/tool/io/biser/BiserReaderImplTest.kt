package ru.capjack.tool.io.biser

import ru.capjack.tool.io.ArrayByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class BiserReaderImplTest {
	@Test
	fun testReadBoolean() {
		testRead(Decoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun testReadByte() {
		testRead(Decoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun testReadInt() {
		testRead(Decoders.INT, DataPairs.int)
	}
	
	@Test
	fun testReadShort() {
		testRead(Decoders.SHORT, DataPairs.short)
	}
	
	@Test
	fun testReadLong() {
		testRead(Decoders.LONG, DataPairs.long)
	}
	
	@Test
	fun testReadDouble() {
		testRead(Decoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun testReadBooleanArray() {
		testRead(Decoders.BOOLEAN_ARRAY, DataPairs.booleanArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadByteArray() {
		testRead(Decoders.BYTE_ARRAY, DataPairs.byteArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadShortArray() {
		testRead(Decoders.SHORT_ARRAY, DataPairs.shortArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadIntArray() {
		testRead(Decoders.INT_ARRAY, DataPairs.intArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadLongArray() {
		testRead(Decoders.LONG_ARRAY, DataPairs.longArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadDoubleArray() {
		testRead(Decoders.DOUBLE_ARRAY, DataPairs.doubleArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun testReadString() {
		testRead(Decoders.STRING, DataPairs.string)
	}
	
	@Test
	fun testReadEnum() {
		testRead(Decoders.ofEnum(), DataPairs.enum)
	}
	
	@Test
	fun testReadList() {
		testRead(Decoders.ofList(Decoders.STRING), DataPairs.listString)
	}
	
	@Test
	fun testReadListList() {
		testRead(
			Decoders.ofList(Decoders.ofList(Decoders.INT)),
			DataPairs.listListInt
		)
	}
	
	@Test
	fun testReadListArray() {
		testRead(Decoders.ofList(Decoders.INT_ARRAY), DataPairs.listIntArray) { expected, actual ->
			assertEquals(expected.size, actual.size, "Size")
			repeat(expected.size) {
				assertPrimitiveArrayEquals(expected[it], actual[it])
			}
		}
	}
	
	private fun <T> testRead(decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>) {
		testRead(decoder, pairs, { expected, actual -> assertEquals(expected, actual) })
	}
	
	private fun <T> testRead(decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>, assert: (T, T) -> Unit) {
		val buffer = ArrayByteBuffer()
		val reader = BiserReaderImpl(buffer)
		
		for (pair in pairs) {
			buffer.clear()
			buffer.writeArray(pair.second)
			
			assert(pair.first, decoder(reader))
		}
	}
}