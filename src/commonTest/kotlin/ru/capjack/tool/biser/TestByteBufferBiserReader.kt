package ru.capjack.tool.biser

import ru.capjack.tool.io.ArrayByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals

class TestByteBufferBiserReader {
	@Test
	fun readBoolean() {
		read(Decoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun readByte() {
		read(Decoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun readInt() {
		read(Decoders.INT, DataPairs.int)
	}
	
	@Test
	fun readLong() {
		read(Decoders.LONG, DataPairs.long)
	}
	
	@Test
	fun readDouble() {
		read(Decoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun readBooleanArray() {
		read(Decoders.BOOLEAN_ARRAY, DataPairs.booleanArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun readByteArray() {
		read(Decoders.BYTE_ARRAY, DataPairs.byteArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun readIntArray() {
		read(Decoders.INT_ARRAY, DataPairs.intArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun readLongArray() {
		read(Decoders.LONG_ARRAY, DataPairs.longArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun readDoubleArray() {
		read(Decoders.DOUBLE_ARRAY, DataPairs.doubleArray, ::assertPrimitiveArrayEquals)
	}
	
	@Test
	fun readString() {
		read(Decoders.STRING, DataPairs.string)
	}
	
	@Test
	fun readStringNullable() {
		read(Decoders.STRING_NULLABLE, DataPairs.stringNullable)
	}
	
	@Test
	fun readList() {
		read(Decoders.forList(Decoders.STRING), DataPairs.listString)
	}
	
	@Test
	fun readListList() {
		read(
			Decoders.forList(Decoders.forList(Decoders.INT)),
			DataPairs.listListInt
		)
	}
	
	@Test
	fun readListArray() {
		read(Decoders.forList(Decoders.INT_ARRAY), DataPairs.listIntArray) { expected, actual ->
			assertEquals(expected.size, actual.size, "Size")
			repeat(expected.size) {
				assertPrimitiveArrayEquals(expected[it], actual[it])
			}
		}
	}
	
	@Test
	fun readMap() {
		read(Decoders.forMap(Decoders.INT, Decoders.STRING), DataPairs.map)
	}
	
	private fun <T> read(decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>) {
		read(decoder, pairs) { expected, actual -> assertEquals(expected, actual) }
	}
	
	private fun <T> read(decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>, assert: (T, T) -> Unit) {
		val buffer = ArrayByteBuffer()
		val reader = ByteBufferBiserReader(buffer)
		
		for (pair in pairs) {
			buffer.clear()
			buffer.writeArray(pair.second)
			
			assert(pair.first, decoder(reader))
		}
	}
}