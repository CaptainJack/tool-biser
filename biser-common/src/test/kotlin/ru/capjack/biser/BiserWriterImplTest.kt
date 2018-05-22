package ru.capjack.biser

import kotlin.test.Test

class BiserWriterImplTest() {
	@Test
	fun testWriteBoolean() {
		testWrite(BiserWriter::writeBoolean, Encoders.BOOLEAN, DataPairs.boolean)
	}
	
	@Test
	fun testWriteByte() {
		testWrite(BiserWriter::writeByte, Encoders.BYTE, DataPairs.byte)
	}
	
	@Test
	fun testWriteInt() {
		testWrite(BiserWriter::writeInt, Encoders.INT, DataPairs.int)
	}
	
	@Test
	fun testWriteLong() {
		testWrite(BiserWriter::writeLong, Encoders.LONG, DataPairs.long)
	}
	
	@Test
	fun testWriteDouble() {
		testWrite(BiserWriter::writeDouble, Encoders.DOUBLE, DataPairs.double)
	}
	
	@Test
	fun testWriteBytes() {
		testWrite(BiserWriter::writeBytes, Encoders.BYTES, DataPairs.bytes)
	}
	
	@Test
	fun testWriteString() {
		testWrite(BiserWriter::writeString, Encoders.STRING, DataPairs.string)
	}
	
	@Test
	fun testWriteEnum() {
		testWrite(BiserWriter::writeEnum, Encoders.ENUM, DataPairs.enum)
	}
	
	@Test
	fun testWriteListString() {
		testWrite(
			{ w, v -> w.writeList(v, Encoders.STRING) },
			Encoders.list(Encoders.STRING),
			DataPairs.listString
		)
	}
	
	@Test
	fun testWriteListListInt() {
		testWrite(
			{ w, v -> w.writeList(v, Encoders.list(Encoders.INT)) },
			Encoders.list(Encoders.list(Encoders.INT)),
			DataPairs.listListInt
		)
	}
	
	private fun <T : Any> testWrite(method: (BiserWriter, T) -> Unit, encoder: Encoder<T>, pairs: List<Pair<T, ByteArray>>) {
		val stream = ByteArrayBiserOutputStream()
		val writer = BiserWriterImpl(stream)
		
		for (pair in pairs) {
			method.invoke(writer, pair.first)
			assertByteArrayEquals(pair.second, stream.toArray())
			stream.clear()
			
			writer.write(pair.first, encoder)
			assertByteArrayEquals(pair.second, stream.toArray())
			stream.clear()
		}
	}
	
}