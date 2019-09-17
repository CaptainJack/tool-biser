package ru.capjack.tool.io.biser

import ru.capjack.tool.io.OutputByteBuffer
import ru.capjack.tool.io.biser.Bytes.x00
import ru.capjack.tool.io.biser.Bytes.x01
import ru.capjack.tool.io.biser.Bytes.xFE
import ru.capjack.tool.io.biser.Bytes.xFF

class BiserWriterImpl(
	var buffer: OutputByteBuffer
) : BiserWriter {
	
	private val tmp = ByteArray(9)
	
	override fun writeBoolean(value: Boolean) {
		buffer.writeByte(if (value) x01 else x00)
	}
	
	override fun writeByte(value: Byte) {
		buffer.writeByte(value)
	}
	
	override fun writeShort(value: Short) {
		tmp[0] = value.asByte(8)
		tmp[1] = value.toByte()
		buffer.writeArray(tmp, size = 2)
	}
	
	override fun writeInt(value: Int) {
		when (value) {
			in 0..253 -> buffer.writeByte(value.toByte())
			-1        -> buffer.writeByte(xFF)
			else      -> {
				tmp[0] = xFE
				tmp[1] = value.asByte(24)
				tmp[2] = value.asByte(16)
				tmp[3] = value.asByte(8)
				tmp[4] = value.toByte()
				buffer.writeArray(tmp, size = 5)
			}
		}
	}
	
	override fun writeLong(value: Long) {
		when (value) {
			in 0..253 -> buffer.writeByte(value.toByte())
			-1L       -> buffer.writeByte(xFF)
			else      -> {
				tmp[0] = xFE
				tmp[1] = value.asByte(56)
				tmp[2] = value.asByte(48)
				tmp[3] = value.asByte(40)
				tmp[4] = value.asByte(32)
				tmp[5] = value.asByte(24)
				tmp[6] = value.asByte(16)
				tmp[7] = value.asByte(8)
				tmp[8] = value.toByte()
				buffer.writeArray(tmp, size = 9)
			}
		}
	}
	
	override fun writeDouble(value: Double) {
		val l = value.toRawBits()
		tmp[0] = l.asByte(56)
		tmp[1] = l.asByte(48)
		tmp[2] = l.asByte(40)
		tmp[3] = l.asByte(32)
		tmp[4] = l.asByte(24)
		tmp[5] = l.asByte(16)
		tmp[6] = l.asByte(8)
		tmp[7] = l.toByte()
		buffer.writeArray(tmp, size = 8)
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
			value.forEach {
				if (it) {
					byte = byte.or(1.shl(bit))
				}
				if (++bit == 8) {
					bytes[s++] = byte.toByte()
					bit = 0
					byte = 0
				}
			}
			
			if (bit != 0) {
				bytes[s] = byte.toByte()
			}
			
			buffer.writeArray(bytes)
		}
	}
	
	override fun writeByteArray(value: ByteArray) {
		writeInt(value.size)
		buffer.writeArray(value)
	}
	
	override fun writeShortArray(value: ShortArray) {
		val size = value.size
		writeInt(size)
		
		if (size != 0) {
			val buffer = ByteArray(size * 2)
			var s = 0
			var b = 0
			while (s < size) {
				val v = value[s++]
				buffer[b] = v.asByte(8)
				buffer[b + 1] = v.toByte()
				b += 2
			}
			this.buffer.writeArray(buffer)
		}
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
		val size = value.size
		writeInt(size)
		
		if (size != 0) {
			val buffer = ByteArray(size * 8)
			var s = 0
			var b = 0
			while (s < size) {
				val v = value[s++].toRawBits()
				buffer[b] = v.asByte(56)
				buffer[b + 1] = v.asByte(48)
				buffer[b + 2] = v.asByte(40)
				buffer[b + 3] = v.asByte(32)
				buffer[b + 4] = v.asByte(24)
				buffer[b + 5] = v.asByte(16)
				buffer[b + 6] = v.asByte(8)
				buffer[b + 7] = v.toByte()
				b += 8
			}
			this.buffer.writeArray(buffer)
		}
	}
	
	override fun writeString(value: String?) {
		when {
			value == null   -> writeInt(-1)
			value.isEmpty() -> writeInt(0)
			else            -> writeByteArray(value.toUtf8ByteArray())
		}
	}
	
	override fun writeEnum(value: Enum<*>) {
		writeInt(value.ordinal)
	}
	
	override fun <E> writeList(value: List<E>, encoder: Encoder<E>) {
		writeInt(value.size)
		value.forEach { encoder(this, it) }
	}
	
	override fun <T> write(value: T, encoder: Encoder<T>) {
		encoder(this, value)
	}
	
	private fun Short.asByte(ushr: Int): Byte {
		return toInt().ushr(ushr).toByte()
	}
	
	private fun Int.asByte(ushr: Int): Byte {
		return ushr(ushr).toByte()
	}
	
	private fun Long.asByte(ushr: Int): Byte {
		return ushr(ushr).toByte()
	}
}