package ru.capjack.biser

import ru.capjack.biser.Bytes.x00
import ru.capjack.biser.Bytes.x01
import ru.capjack.biser.Bytes.xFC
import ru.capjack.biser.Bytes.xFD
import ru.capjack.biser.Bytes.xFE
import ru.capjack.biser.Bytes.xFF

class BiserWriterImpl(
	private val stream: BiserOutputStream
) : BiserWriter {
	private val buffer = ByteArray(9)
	
	override fun writeBoolean(value: Boolean) {
		stream.write(if (value) x01 else x00)
	}
	
	override fun writeByte(value: Byte) {
		stream.write(value)
	}
	
	override fun writeInt(value: Int) {
		when (value) {
			in 0..251             -> {
				stream.write(value.toByte())
			}
			in 252..507           -> {
				buffer[0] = xFC
				buffer[1] = (value - 252).toByte()
				stream.write(buffer, 2)
			}
			in 508..66_043        -> {
				val v = value - 508
				buffer[0] = xFD
				buffer[1] = v.ushr(8).toByte()
				buffer[2] = v.toByte()
				stream.write(buffer, 3)
			}
			in 66_044..16_843_259 -> {
				val v = value - 66_044
				buffer[0] = xFE
				buffer[1] = v.ushr(16).toByte()
				buffer[2] = v.ushr(8).toByte()
				buffer[3] = v.toByte()
				stream.write(buffer, 4)
			}
			else                  -> {
				buffer[0] = xFF
				buffer[1] = value.ushr(24).toByte()
				buffer[2] = value.ushr(16).toByte()
				buffer[3] = value.ushr(8).toByte()
				buffer[4] = value.toByte()
				stream.write(buffer, 5)
			}
		}
	}
	
	override fun writeLong(value: Long) {
		when (value) {
			in 0..16_843_259 -> writeInt(value.toInt())
			else             -> {
				buffer[0] = xFF
				buffer[1] = value.ushr(56).toByte()
				buffer[2] = value.ushr(48).toByte()
				buffer[3] = value.ushr(40).toByte()
				buffer[4] = value.ushr(32).toByte()
				buffer[5] = value.ushr(24).toByte()
				buffer[6] = value.ushr(16).toByte()
				buffer[7] = value.ushr(8).toByte()
				buffer[8] = value.toByte()
				stream.write(buffer, 9)
			}
		}
	}
	
	override fun writeDouble(value: Double) {
		val l = value.toRawBits()
		buffer[0] = l.ushr(56).toByte()
		buffer[1] = l.ushr(48).toByte()
		buffer[2] = l.ushr(40).toByte()
		buffer[3] = l.ushr(32).toByte()
		buffer[4] = l.ushr(24).toByte()
		buffer[5] = l.ushr(16).toByte()
		buffer[6] = l.ushr(8).toByte()
		buffer[7] = l.toByte()
		stream.write(buffer, 8)
	}
	
	override fun writeBytes(value: ByteArray) {
		writeInt(value.size)
		stream.write(value)
	}
	
	override fun writeString(value: String) {
		writeBytes(PlatformUtils.encodeStringUtf8(value))
	}
	
	override fun writeEnum(value: Enum<*>) {
		writeInt(value.ordinal)
	}
	
	override fun <E> writeList(value: List<E>, encoder: Encoder<E>) {
		writeInt(value.size)
		value.forEach { encoder.invoke(this, it) }
	}
	
	override fun <T> write(value: T, encoder: Encoder<T>) {
		encoder.invoke(this, value)
	}
}