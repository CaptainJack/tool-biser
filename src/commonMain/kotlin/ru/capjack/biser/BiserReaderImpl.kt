package ru.capjack.biser

import ru.capjack.biser.Bytes.x01
import ru.capjack.biser.Bytes.xFE
import ru.capjack.biser.Bytes.xFF
import ru.capjack.tool.io.InputByteBuffer
import ru.capjack.tool.io.readToArray
import kotlin.reflect.KClass

open class BiserReaderImpl(
	private val target: InputByteBuffer
) : BiserReader {
	private val buffer = ByteArray(8)
	
	override fun readBoolean(): Boolean {
		return readByte() == x01
	}
	
	override fun readByte(): Byte {
		return target.readByte()
	}
	
	override fun readShort(): Short {
		readToBuffer(2)
		return (buffer[0].asInt(8) + buffer[1].asInt()).toShort()
	}
	
	override fun readInt(): Int {
		return when (val b = readByte()) {
			xFF  -> -1
			xFE  -> {
				readToBuffer(4)
				buffer[0].asInt(24) +
					buffer[1].asInt(16) +
					buffer[2].asInt(8) +
					buffer[3].asInt()
			}
			else -> b.asInt()
		}
	}
	
	override fun readLong(): Long {
		return when (val b = readByte()) {
			xFF  -> -1
			xFE  -> readRawLong()
			else -> b.asLong()
		}
	}
	
	override fun readDouble(): Double {
		return Double.fromBits(readRawLong())
	}
	
	override fun readBooleanArray(): BooleanArray {
		var bit = 7
		var byte = 0
		
		return BooleanArray(readInt()) {
			if (++bit == 8) {
				bit = 0
				byte = readByte().asInt()
			}
			val m = 1.shl(bit)
			byte.and(m) == m
		}
	}
	
	override fun readByteArray(): ByteArray {
		val size = readInt()
		return target.readToArray(size)
	}
	
	override fun readShortArray(): ShortArray {
		return ShortArray(readInt()) { readShort() }
	}
	
	override fun readIntArray(): IntArray {
		return IntArray(readInt()) { readInt() }
	}
	
	override fun readLongArray(): LongArray {
		return LongArray(readInt()) { readLong() }
	}
	
	override fun readDoubleArray(): DoubleArray {
		return DoubleArray(readInt()) { readDouble() }
	}
	
	
	override fun readString(): String? {
		val size = readInt()
		if (size == -1) {
			return null
		}
		return target.readToArray(size).toUtf8String()
	}
	
	override fun <E : Enum<E>> readEnum(type: KClass<E>): E {
		return type.getEnumValue(readInt())
	}
	
	override fun <E> readList(decoder: Decoder<E>): List<E> {
		return when (val size = readInt()) {
			0    -> emptyList()
			else -> List(size) { decoder(this) }
		}
	}
	
	override fun <E> read(decoder: Decoder<E>): E {
		return decoder(this)
	}
	
	private fun readToBuffer(size: Int) {
		target.readArray(buffer, size = size)
	}
	
	private fun readRawLong(): Long {
		readToBuffer(8)
		
		return buffer[0].asLong(56) +
			buffer[1].asLong(48) +
			buffer[2].asLong(40) +
			buffer[3].asLong(32) +
			buffer[4].asLong(24) +
			buffer[5].asInt(16) +
			buffer[6].asInt(8) +
			buffer[7].asInt()
	}
	
	private fun Byte.asInt(): Int {
		return toInt().and(0xFF)
	}
	
	private fun Byte.asInt(shl: Int): Int {
		return asInt().shl(shl)
	}
	
	private fun Byte.asLong(): Long {
		return toLong().and(0xFF)
	}
	
	private fun Byte.asLong(shl: Int): Long {
		return asLong().shl(shl)
	}
}

