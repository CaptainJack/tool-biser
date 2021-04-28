package ru.capjack.tool.biser

import ru.capjack.tool.io.ArrayByteBuffer
import ru.capjack.tool.io.readArray

inline fun encodeBiser(block: BiserWriter.() -> Unit): ByteArray {
	val buffer = ArrayByteBuffer()
	val writer = ByteBufferBiserWriter(buffer)
	writer.block()
	return buffer.readArray()
}

inline fun <T> decodeBiser(bytes: ByteArray, block: BiserReader.() -> T): T {
	val buffer = ArrayByteBuffer(bytes)
	val reader = ByteBufferBiserReader(buffer)
	return reader.block()
}
