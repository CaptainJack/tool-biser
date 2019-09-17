package ru.capjack.biser

import ru.capjack.biser.Bytes.x00
import ru.capjack.biser.Bytes.x01
import ru.capjack.biser.Bytes.xFE
import ru.capjack.biser.Bytes.xFF
import ru.capjack.tool.io.DummyOutputByteBuffer
import ru.capjack.tool.io.OutputByteBuffer

class BiserWriterImpl(
	var target: OutputByteBuffer = DummyOutputByteBuffer
) : BiserWriter {
	
	private val buffer = ByteArray(9)
	
	override fun writeBoolean(value: Boolean) {
		target.writeByte(if (value) x01 else x00)
	}
	
	override fun writeByte(value: Byte) {
		target.writeByte(value)
	}
	
	override fun writeShort(value: Short) {
		buffer[0] = value.asByte(8)
		buffer[1] = value.toByte()
		target.writeArray(buffer, size = 2)
	}
	
	override fun writeInt(value: Int) {
		when (value) {
			in 0..253 -> target.writeByte(value.toByte())
			-1        -> target.writeByte(xFF)
			else      -> {
				buffer[0] = xFE
				buffer[1] = value.asByte(24)
				buffer[2] = value.asByte(16)
				buffer[3] = value.asByte(8)
				buffer[4] = value.toByte()
				target.writeArray(buffer, size = 5)
			}
		}
	}
	
	override fun writeLong(value: Long) {
		when (value) {
			in 0..253 -> target.writeByte(value.toByte())
			-1L       -> target.writeByte(xFF)
			else      -> {
				buffer[0] = xFE
				buffer[1] = value.asByte(56)
				buffer[2] = value.asByte(48)
				buffer[3] = value.asByte(40)
				buffer[4] = value.asByte(32)
				buffer[5] = value.asByte(24)
				buffer[6] = value.asByte(16)
				buffer[7] = value.asByte(8)
				buffer[8] = value.toByte()
				target.writeArray(buffer, size = 9)
			}
		}
	}
	
	override fun writeDouble(value: Double) {
		val l = value.toRawBits()
		buffer[0] = l.asByte(56)
		buffer[1] = l.asByte(48)
		buffer[2] = l.asByte(40)
		buffer[3] = l.asByte(32)
		buffer[4] = l.asByte(24)
		buffer[5] = l.asByte(16)
		buffer[6] = l.asByte(8)
		buffer[7] = l.toByte()
		target.writeArray(buffer, size = 8)
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
			
			target.writeArray(bytes)
		}
	}
	
	override fun writeByteArray(value: ByteArray) {
		writeInt(value.size)
		target.writeArray(value)
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
			target.writeArray(buffer)
		}
	}
	
	override fun writeIntArray(value: IntArray) {
		target.ensureWrite(1 + value.size)
		writeInt(value.size)
		value.forEach(::writeInt)
	}
	
	override fun writeLongArray(value: LongArray) {
		target.ensureWrite(1 + value.size)
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
			target.writeArray(buffer)
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