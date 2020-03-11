package ru.capjack.tool.io.biser

import ru.capjack.tool.io.OutputByteBuffer
import ru.capjack.tool.io.ensureWriteableArrayView

class ByteBufferBiserWriter(var buffer: OutputByteBuffer) : AbstractBiserWriter() {
	
	override fun writeByte(value: Byte) {
		buffer.writeByte(value)
	}
	
	override fun writeByteArrayRaw(array: ByteArray, size: Int) {
		buffer.writeArray(array, 0, size)
	}
	
	override fun writeByteArray(value: ByteArray) {
		buffer.ensureWrite(1 + value.size)
		super.writeByteArray(value)
	}
	
	override fun writeIntArray(value: IntArray) {
		buffer.ensureWrite(1 + value.size)
		super.writeIntArray(value)
	}
	
	override fun writeLongArray(value: LongArray) {
		buffer.ensureWrite(1 + value.size)
		super.writeLongArray(value)
	}
	
	override fun writeDoubleArray(value: DoubleArray) {
		buffer.ensureWrite(1 + value.size * 8)
		super.writeDoubleArray(value)
	}
	
	override fun writeString(value: String) {
		if (value.isEmpty()) {
			writeInt(0)
		}
		else {
			val view = buffer.ensureWriteableArrayView(value.length * 4 + 5)
			val array = view.array
			val index = view.writerIndex
			
			val size = value.encodeToUtf8ByteArray(array, index)
			
			val shift = if (size <= 253) 1 else 5
			array.copyInto(array, index + shift, index, index + size)
			writeInt(size)
			view.commitWrite(size)
		}
	}
}