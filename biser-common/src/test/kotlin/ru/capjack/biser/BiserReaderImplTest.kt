package ru.capjack.biser

import kotlin.test.Test
import kotlin.test.assertEquals

class BiserReaderImplTest {
	@Test
	fun testReadBoolean() {
		testRead(BiserReader::readBoolean, Decoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun testReadByte() {
		testRead(BiserReader::readByte, Decoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun testReadInt() {
		testRead(BiserReader::readInt, Decoders.INT, DataPairs.int)
	}
	
	@Test
	fun testReadLong() {
		testRead(BiserReader::readLong, Decoders.LONG, DataPairs.long)
	}
	
	@Test
	fun testReadDouble() {
		testRead(BiserReader::readDouble, Decoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun testReadBytes() {
		testRead(BiserReader::readBytes, Decoders.BYTES, DataPairs.bytes, ::assertByteArrayEquals)
	}
	
	@Test
	fun testReadString() {
		testRead(BiserReader::readString, Decoders.STRING, DataPairs.string)
	}
	
	@Test
	fun testReadEnum() {
		testRead({ it.readEnum(EnumStub::class) }, Decoders.enum(), DataPairs.enum)
	}
	
	@Test
	fun testReadListString() {
		testRead({ it.readList(Decoders.STRING) }, Decoders.list(Decoders.STRING), DataPairs.listString)
	}
	
	@Test
	fun testReadListListInt() {
		testRead({ it.readList(Decoders.list(Decoders.INT)) }, Decoders.list(Decoders.list(Decoders.INT)), DataPairs.listListInt)
	}
	
	private fun <T : Any> testRead(method: (reader: BiserReader) -> T, decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>) {
		testRead(method, decoder, pairs, { expected, actual -> assertEquals(expected, actual) })
	}
	
	private fun <T : Any> testRead(method: (reader: BiserReader) -> T, decoder: Decoder<T>, pairs: List<Pair<T, ByteArray>>, assert: (T, T) -> Unit) {
		val stream = ByteArrayBiserInputStream()
		val reader = BiserReaderImpl(stream)
		
		for (pair in pairs) {
			stream.reset(pair.second)
			assert(pair.first, method.invoke(reader))
			
			stream.reset()
			assert(pair.first, decoder.invoke(reader))
		}
	}
}