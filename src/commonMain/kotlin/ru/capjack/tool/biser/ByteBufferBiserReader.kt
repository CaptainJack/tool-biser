package ru.capjack.tool.biser

import ru.capjack.tool.io.InputByteBuffer
import ru.capjack.tool.io.readArray

class ByteBufferBiserReader(var buffer: InputByteBuffer) : AbstractBiserReader() {
	override fun readByte(): Byte {
		return buffer.readByte()
	}
	
	override fun readByteArray(target: ByteArray, offset: Int, size: Int) {
		return buffer.readToArray(target, offset, size)
	}
	
	override fun readString(): String {
		val size = readInt()
		return readString(size)
	}
	
	override fun readStringNullable(): String? {
		val size = readInt()
		return if (size == -1) null else readString(size)
	}
	
	override fun readToMemory(size: Int) {
		buffer.readToArray(memory, 0, size)
	}
	
	private fun readString(size: Int): String {
		if (size == 0)
			return ""
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		val view = buffer.arrayView
		if (view == null) {
			val bytes = buffer.readArray(size)
			return bytes.decodeToString()
		}
		
		val value = view.array.decodeToUtf8String(view.readerIndex, size)
		buffer.skipRead(size)
		return value
	}
}

