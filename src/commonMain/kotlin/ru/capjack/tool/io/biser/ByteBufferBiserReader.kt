package ru.capjack.tool.io.biser

import ru.capjack.tool.io.InputByteBuffer
import ru.capjack.tool.io.readToArray

class ByteBufferBiserReader(
	private val buffer: InputByteBuffer
) : BiserReader {
	
	private val tmp = ByteArray(8)
	
	override fun readBoolean(): Boolean {
		return readByte() == x01
	}
	
	override fun readByte(): Byte {
		return buffer.readByte()
	}
	
	override fun readInt(): Int {
		return when (val b = readByte()) {
			xFF  -> -1
			xFE  -> {
				readToTmp(4)
				(tmp[0].asInt() shl 24)
					.or(tmp[1].asInt() shl 16)
					.or(tmp[2].asInt() shl 8)
					.or(tmp[3].asInt())
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
			val m = 1 shl bit
			byte and m == m
		}
	}
	
	override fun readByteArray(): ByteArray {
		val size = readInt()
		return buffer.readToArray(size)
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
	
	
	override fun readString(): String {
		val size = readInt()
		val view = buffer.readableArrayView
		val value = view.array.decodeToUtf8String(view.readerIndex, size)
		view.commitRead(size)
		return value
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
	
	private fun readToTmp(size: Int) {
		buffer.readArray(tmp, size = size)
	}
	
	private fun readRawLong(): Long {
		readToTmp(8)
		
		return (tmp[0].asLong() shl 56)
			.or(tmp[1].asLong() shl 48)
			.or(tmp[2].asLong() shl 40)
			.or(tmp[3].asLong() shl 32)
			.or(tmp[4].asLong() shl 24)
			.or(tmp[5].asLong() shl 16)
			.or(tmp[6].asLong() shl 8)
			.or(tmp[7].asLong())
	}
	
	private fun Byte.asLong(): Long {
		return toLong().and(0xFF)
	}
}

