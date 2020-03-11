package ru.capjack.tool.io.biser

import ru.capjack.tool.io.InputByteBuffer
import ru.capjack.tool.io.readToArray

class ByteBufferBiserReader(var buffer: InputByteBuffer) : AbstractBiserReader() {
	override fun readByte(): Byte {
		return buffer.readByte()
	}
	
	override fun readToMemory(size: Int) {
		buffer.readArray(memory, 0, size)
	}
	
	override fun readByteArray(size: Int): ByteArray {
		return buffer.readToArray(size)
	}
	
	override fun readString(): String {
		val size = readInt()
		val view = buffer.readableArrayView
		val value = view.array.decodeToUtf8String(view.readerIndex, size)
		view.commitRead(size)
		return value
	}
}

