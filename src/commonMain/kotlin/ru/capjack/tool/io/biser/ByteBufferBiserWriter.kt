package ru.capjack.tool.io.biser

import ru.capjack.tool.io.OutputByteBuffer
import ru.capjack.tool.io.ensureWriteableArrayView

class ByteBufferBiserWriter(
	private val buffer: OutputByteBuffer
) : BiserWriter {
	
	private val tmp = ByteArray(9)
	
	override fun writeBoolean(value: Boolean) {
		buffer.writeByte(if (value) x01 else x00)
	}
	
	override fun writeByte(value: Byte) {
		buffer.writeByte(value)
	}
	
	@Suppress("ConvertTwoComparisonsToRangeCheck", "CascadeIf")
	override fun writeInt(value: Int) {
		if (value >= 0) {
			if (value < 128) {
				buffer.writeByte(value.toByte())
			}
			else if (value < 16512) {
				val v = value - 128
				tmp[0] = v ushr 8 or 0x80
				tmp[1] = v
				buffer.writeArray(tmp, 0, 2)
			}
			else if (value < 2113664) {
				val v = value - 16512
				tmp[0] = v ushr 16 or 0xC0
				tmp[1] = v ushr 8
				tmp[2] = v
				buffer.writeArray(tmp, 0, 3)
			}
			else if (value < 270549120) {
				val v = value - 2113664
				tmp[0] = v ushr 24 or 0xE0
				tmp[1] = v ushr 16
				tmp[2] = v ushr 8
				tmp[3] = v
				buffer.writeArray(tmp, 0, 4)
			}
			else if (value < 404766848) {
				val v = value - 270549120
				tmp[0] = v ushr 24 or 0xF0
				tmp[1] = v ushr 16
				tmp[2] = v ushr 8
				tmp[3] = v
				buffer.writeArray(tmp, 0, 4)
			}
			else if (value < 471875712) {
				val v = value - 404766848
				tmp[0] = v ushr 24 or 0xF8
				tmp[1] = v ushr 16
				tmp[2] = v ushr 8
				tmp[3] = v
				buffer.writeArray(tmp, 0, 4)
			}
			else {
				tmp[0] = xFE
				tmp[1] = value ushr 24
				tmp[2] = value ushr 16
				tmp[3] = value ushr 8
				tmp[4] = value
				buffer.writeArray(tmp, 0, 5)
			}
		}
		else if (value == -1) {
			buffer.writeByte(xFF)
		}
		else if (value >= -33554433) {
			val v = (value + 1).and(0x1FFFFFF)
			tmp[0] = v ushr 24 or 0xFC
			tmp[1] = v ushr 16
			tmp[2] = v ushr 8
			tmp[3] = v
			buffer.writeArray(tmp, 0, 4)
		}
		else {
			tmp[0] = xFE
			tmp[1] = value ushr 24
			tmp[2] = value ushr 16
			tmp[3] = value ushr 8
			tmp[4] = value
			buffer.writeArray(tmp, 0, 5)
		}
	}
	
	
	override fun writeLong(value: Long) {
		if (value >= -33554433 && value < 471875712) {
			writeInt(value.toInt())
		}
		else {
			tmp[0] = xFE
			tmp[1] = value ushr 56
			tmp[2] = value ushr 48
			tmp[3] = value ushr 40
			tmp[4] = value ushr 32
			tmp[5] = value ushr 24
			tmp[6] = value ushr 16
			tmp[7] = value ushr 8
			tmp[8] = value
			buffer.writeArray(tmp, 0, 9)
		}
	}
	
	override fun writeDouble(value: Double) {
		val l = value.toRawBits()
		tmp[0] = l ushr 56
		tmp[1] = l ushr 48
		tmp[2] = l ushr 40
		tmp[3] = l ushr 32
		tmp[4] = l ushr 24
		tmp[5] = l ushr 16
		tmp[6] = l ushr 8
		tmp[7] = l
		buffer.writeArray(tmp, 0, 8)
	}
	
	override fun writeBooleanArray(value: BooleanArray) {
		var s = value.size
		writeInt(s)
		if (s != 0) {
			s /= 8
			if (value.size % 8 != 0) {
				s += 1
			}
			
			val bytes = ByteArray(s)
			var byte = 0
			var bit = 0
			s = 0
			
			for (v in value) {
				if (v) {
					byte = byte or (1 shl bit)
				}
				if (++bit == 8) {
					bytes[s++] = byte
					bit = 0
					byte = 0
				}
			}
			
			if (bit != 0) {
				bytes[s] = byte
			}
			
			buffer.writeArray(bytes, 0, bytes.size)
		}
	}
	
	override fun writeByteArray(value: ByteArray) {
		buffer.ensureWrite(1 + value.size)
		writeInt(value.size)
		buffer.writeArray(value, 0, value.size)
	}
	
	override fun writeIntArray(value: IntArray) {
		buffer.ensureWrite(1 + value.size)
		writeInt(value.size)
		value.forEach(::writeInt)
	}
	
	override fun writeLongArray(value: LongArray) {
		buffer.ensureWrite(1 + value.size)
		writeInt(value.size)
		value.forEach(::writeLong)
	}
	
	override fun writeDoubleArray(value: DoubleArray) {
		buffer.ensureWrite(1 + value.size * 8)
		
		val size = value.size
		writeInt(size)
		
		if (size != 0) {
			val arr = ByteArray(size * 8)
			var s = 0
			var b = 0
			while (s < size) {
				val v = value[s++].toRawBits()
				arr[b] = v ushr 56
				arr[b + 1] = v ushr 48
				arr[b + 2] = v ushr 40
				arr[b + 3] = v ushr 32
				arr[b + 4] = v ushr 24
				arr[b + 5] = v ushr 16
				arr[b + 6] = v ushr 8
				arr[b + 7] = v
				b += 8
			}
			buffer.writeArray(arr, 0, arr.size)
		}
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
	
	override fun <E> writeList(value: List<E>, encoder: Encoder<E>) {
		writeInt(value.size)
		value.forEach { encoder(this, it) }
	}
	
	override fun <T> write(value: T, encoder: Encoder<T>) {
		encoder(value)
	}
}