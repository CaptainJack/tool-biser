package ru.capjack.biser

import ru.capjack.biser.Bytes.x01
import ru.capjack.biser.Bytes.xFC
import ru.capjack.biser.Bytes.xFD
import ru.capjack.biser.Bytes.xFE
import ru.capjack.biser.Bytes.xFF
import kotlin.reflect.KClass

class BiserReaderImpl(
	private val stream: BiserInputStream
) : BiserReader {
	private val buffer = ByteArray(8)
	
	override fun readBoolean(): Boolean {
		return nextByte() == x01
	}
	
	override fun readByte(): Byte {
		return nextByte()
	}
	
	override fun readInt(): Int {
		return readInt(nextByte())
	}
	
	private fun readInt(b: Byte): Int {
		return when (b) {
			xFC  -> {
				252 +
					nextByte().toUint()
			}
			xFD  -> {
				nextBytesToBuffer(2)
				508 +
					buffer[0].toUint().shl(8) +
					buffer[1].toUint()
			}
			xFE  -> {
				nextBytesToBuffer(3)
				66_044 +
					buffer[0].toUint().shl(16) +
					buffer[1].toUint().shl(8) +
					buffer[2].toUint()
			}
			xFF  -> {
				nextBytesToBuffer(4)
				0 +
					buffer[0].toUint().shl(24) +
					buffer[1].toUint().shl(16) +
					buffer[2].toUint().shl(8) +
					buffer[3].toUint()
			}
			else -> b.toUint()
		}
	}
	
	override fun readLong(): Long {
		val b = nextByte()
		return when (b) {
			xFF  -> readRawLong()
			else -> readInt(b).toLong()
		}
	}
	
	override fun readDouble(): Double {
		return Double.fromBits(readRawLong())
	}
	
	override fun readBytes(): ByteArray {
		val size = readInt()
		return when (size) {
			0    -> ByteArray(0)
			else -> ByteArray(size).also { stream.read(it) }
		}
	}
	
	override fun readString(): String {
		return PlatformUtils.decodeStringUtf8(readBytes())
	}
	
	override fun <E : Enum<E>> readEnum(type: KClass<E>): E {
		return PlatformUtils.getEnumValue(type, readInt())
	}
	
	override fun <E> readList(decoder: Decoder<E>): List<E> {
		val size = readInt()
		return when (size) {
			0    -> emptyList()
			else -> List(size) { decoder.invoke(this) }
		}
	}
	
	override fun <E> read(decoder: Decoder<E>): E {
		return decoder.invoke(this)
	}
	
	private fun nextByte(): Byte {
		return stream.read()
	}
	
	private fun readRawLong(): Long {
		nextBytesToBuffer(8)
		
		return buffer[0].toLong().shl(56) +
			buffer[1].toUlong().shl(48) +
			buffer[2].toUlong().shl(40) +
			buffer[3].toUlong().shl(32) +
			buffer[4].toUlong().shl(24) +
			buffer[5].toUint().shl(16) +
			buffer[6].toUint().shl(8) +
			buffer[7].toUint()
	}
	
	private fun nextBytesToBuffer(len: Int) {
		stream.read(buffer, len)
	}
	
	private fun Byte.toUlong(): Long {
		return toLong().and(0xFF)
	}
}