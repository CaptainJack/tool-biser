package ru.capjack.tool.io.biser

import ru.capjack.tool.io.ArrayByteBuffer
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class TestIntReadWrite {
	@Test
	@Ignore
	fun allIntegers() {
		val buffer = ArrayByteBuffer(5)
		val writer = ByteBufferBiserWriter(buffer)
		val reader = ByteBufferBiserReader(buffer)
		
		var expected = Int.MIN_VALUE
		
		while (true) {
			buffer.clear()
			val actual = try {
				writer.writeInt(expected)
				reader.readInt()
			}
			catch (e: Throwable) {
				throw RuntimeException("Integer: $expected", e)
			}
			assertEquals(expected, actual)
			
			if (++expected == Int.MIN_VALUE) {
				break
			}
		}
	}
	
	@Test
	@Ignore
	fun manyLongs() {
		val buffer = ArrayByteBuffer(9)
		val writer = ByteBufferBiserWriter(buffer)
		val reader = ByteBufferBiserReader(buffer)
		
		var expected = Int.MIN_VALUE.toLong() - 1000000
		val end = Int.MAX_VALUE.toLong() + 1000000
		
		while (true) {
			buffer.clear()
			val actual = try {
				writer.writeLong(expected)
				reader.readLong()
			}
			catch (e: Throwable) {
				throw RuntimeException("Long: $expected", e)
			}
			assertEquals(expected, actual)
			
			if (++expected == end) {
				break
			}
		}
	}
}