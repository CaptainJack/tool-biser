package ru.capjack.tool.biser

import ru.capjack.tool.io.OutputByteBuffer
import kotlin.jvm.Volatile

class ByteBufferBiserWriter(@Volatile var buffer: OutputByteBuffer) : AbstractBiserWriter() {
	
	override fun writeByte(value: Byte) {
		buffer.writeByte(value)
	}
	
	override fun writeByteArrayRaw(array: ByteArray, offset: Int, size: Int) {
		buffer.writeArray(array, offset, size)
	}
	
	override fun writeByteArray(value: ByteArray, offset: Int, size: Int) {
		buffer.ensureWrite(1 + value.size)
		super.writeByteArray(value, offset, size)
	}
	
	override fun writeIntArray(value: IntArray, offset: Int, size: Int) {
		buffer.ensureWrite(1 + value.size)
		super.writeIntArray(value, offset, size)
	}
	
	override fun writeLongArray(value: LongArray, offset: Int, size: Int) {
		buffer.ensureWrite(1 + value.size)
		super.writeLongArray(value, offset, size)
	}
	
	override fun writeDoubleArray(value: DoubleArray, offset: Int, size: Int) {
		buffer.ensureWrite(1 + value.size * 8)
		super.writeDoubleArray(value, offset, size)
	}
	
	override fun writeString(value: String) {
		if (value.isEmpty()) {
			writeInt(0)
		}
		else {
			val view = buffer.arrayView
			if (view == null) {
				val bytes = value.encodeToByteArray()
				writeByteArray(bytes)
			}
			else {
				buffer.ensureWrite(value.length * 4 + 5)
				
				val array = view.array
				val index = view.writerIndex
				
				val size = value.encodeToUtf8ByteArray(array, index)
				
				val shift = when {
					size < 128       -> 1
					size < 16512     -> 2
					size < 2113664   -> 3
					size < 471875712 -> 4
					else             -> 5
				}
				array.copyInto(array, index + shift, index, index + size)
				writeInt(size)
				
				buffer.skipWrite(size)
			}
		}
	}
	
	override fun writeStringNullable(value: String?) {
		if (value == null) {
			writeInt(-1)
		}
		else {
			writeString(value)
		}
	}
}