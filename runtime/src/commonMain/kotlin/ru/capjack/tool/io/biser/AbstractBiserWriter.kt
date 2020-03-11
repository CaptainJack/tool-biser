package ru.capjack.tool.io.biser

abstract class AbstractBiserWriter : BiserWriter {
	private val memory = ByteArray(9)
	
	override fun writeBoolean(value: Boolean) {
		writeByte(if (value) x01 else x00)
	}
	
	@Suppress("ConvertTwoComparisonsToRangeCheck", "CascadeIf")
	override fun writeInt(value: Int) {
		if (value >= 0) {
			if (value < 128) {
				writeByte(value.toByte())
			}
			else if (value < 16512) {
				val v = value - 128
				memory[0] = v ushr 8 or 0x80
				memory[1] = v
				writeByteArrayRaw(memory, 2)
			}
			else if (value < 2113664) {
				val v = value - 16512
				memory[0] = v ushr 16 or 0xC0
				memory[1] = v ushr 8
				memory[2] = v
				writeByteArrayRaw(memory, 3)
			}
			else if (value < 270549120) {
				val v = value - 2113664
				memory[0] = v ushr 24 or 0xE0
				memory[1] = v ushr 16
				memory[2] = v ushr 8
				memory[3] = v
				writeByteArrayRaw(memory, 4)
			}
			else if (value < 404766848) {
				val v = value - 270549120
				memory[0] = v ushr 24 or 0xF0
				memory[1] = v ushr 16
				memory[2] = v ushr 8
				memory[3] = v
				writeByteArrayRaw(memory, 4)
			}
			else if (value < 471875712) {
				val v = value - 404766848
				memory[0] = v ushr 24 or 0xF8
				memory[1] = v ushr 16
				memory[2] = v ushr 8
				memory[3] = v
				writeByteArrayRaw(memory, 4)
			}
			else {
				memory[0] = xFE
				memory[1] = value ushr 24
				memory[2] = value ushr 16
				memory[3] = value ushr 8
				memory[4] = value
				writeByteArrayRaw(memory, 5)
			}
		}
		else if (value == -1) {
			writeByte(xFF)
		}
		else if (value >= -33554433) {
			val v = (value + 1).and(0x1FFFFFF)
			memory[0] = v ushr 24 or 0xFC
			memory[1] = v ushr 16
			memory[2] = v ushr 8
			memory[3] = v
			writeByteArrayRaw(memory, 4)
		}
		else {
			memory[0] = xFE
			memory[1] = value ushr 24
			memory[2] = value ushr 16
			memory[3] = value ushr 8
			memory[4] = value
			writeByteArrayRaw(memory, 5)
		}
	}
	
	override fun writeLong(value: Long) {
		if (value >= -33554433 && value < 471875712) {
			writeInt(value.toInt())
		}
		else {
			memory[0] = xFE
			memory[1] = value ushr 56
			memory[2] = value ushr 48
			memory[3] = value ushr 40
			memory[4] = value ushr 32
			memory[5] = value ushr 24
			memory[6] = value ushr 16
			memory[7] = value ushr 8
			memory[8] = value
			writeByteArrayRaw(memory, 9)
		}
	}
	
	override fun writeDouble(value: Double) {
		val l = value.toRawBits()
		memory[0] = l ushr 56
		memory[1] = l ushr 48
		memory[2] = l ushr 40
		memory[3] = l ushr 32
		memory[4] = l ushr 24
		memory[5] = l ushr 16
		memory[6] = l ushr 8
		memory[7] = l
		writeByteArrayRaw(memory, 8)
	}
	
	@Suppress("DuplicatedCode")
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
			
			writeByteArrayRaw(bytes, bytes.size)
		}
	}
	
	override fun writeByteArray(value: ByteArray) {
		writeInt(value.size)
		writeByteArrayRaw(value, value.size)
	}
	
	override fun writeIntArray(value: IntArray) {
		writeInt(value.size)
		value.forEach(::writeInt)
	}
	
	override fun writeLongArray(value: LongArray) {
		writeInt(value.size)
		value.forEach(::writeLong)
	}
	
	override fun writeDoubleArray(value: DoubleArray) {
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
			writeByteArrayRaw(arr, arr.size)
		}
	}
	
	override fun <E> writeList(value: List<E>, encoder: Encoder<E>) {
		writeInt(value.size)
		
		if (encoder === Encoders.BOOLEAN) {
			@Suppress("UNCHECKED_CAST")
			writeBooleanList(value as List<Boolean>)
		}
		else {
			value.forEach { encoder(this, it) }
		}
	}
	
	@Suppress("DuplicatedCode")
	private fun writeBooleanList(value: List<Boolean>) {
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
			
			writeByteArrayRaw(bytes, bytes.size)
		}
	}
	
	override fun <T> write(value: T, encoder: Encoder<T>) {
		encoder(value)
	}
	
	protected abstract fun writeByteArrayRaw(array: ByteArray, size: Int)
}