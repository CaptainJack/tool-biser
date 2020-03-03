package ru.capjack.tool.io.biser

import ru.capjack.tool.io.InputByteBuffer
import ru.capjack.tool.io.readToArray

class ByteBufferBiserReader(
	var buffer: InputByteBuffer
) : BiserReader {
	
	private val tmp = ByteArray(8)
	
	override fun readBoolean(): Boolean {
		return readByte() == x01
	}
	
	override fun readByte(): Byte {
		return buffer.readByte()
	}
	
	override fun readInt(): Int {
		val b = i(buffer.readByte())
		
		when {
			b and 0x80 == 0x00 ->
				return b
			b == 0xFF          ->
				return -1
			b and 0xC0 == 0x80 ->
				return ((b and 0x3F).shl(8) or i(buffer.readByte())) + 128
			b and 0xE0 == 0xC0 -> {
				readToTmp(2)
				return ((b and 0x1F).shl(16) or i(tmp[0]).shl(8) or i(tmp[1])) + 16512
			}
			b and 0xF0 == 0xE0 -> {
				readToTmp(3)
				return ((b and 0x0F).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 2113664
			}
			b and 0xF8 == 0xF0 -> {
				readToTmp(3)
				return ((b and 0x07).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 270549120
			}
			b and 0xFC == 0xF8 -> {
				readToTmp(3)
				return ((b and 0x03).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 404766848
			}
			b and 0xFE == 0xFC -> {
				readToTmp(3)
				return ((b and 0x01 or 0xFE).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) - 1
			}
			else               -> {
				readToTmp(4)
				return (i(tmp[0]) shl 24)
					.or(i(tmp[1]) shl 16)
					.or(i(tmp[2]) shl 8)
					.or(i(tmp[3]))
			}
		}
	}
	
	override fun readLong(): Long {
		val b = i(buffer.readByte())
		
		when {
			b and 0x80 == 0x00 ->
				return b.toLong()
			b == 0xFF          ->
				return -1
			b and 0xC0 == 0x80 ->
				return (((b and 0x3F).shl(8) or i(buffer.readByte())) + 128).toLong()
			b and 0xE0 == 0xC0 -> {
				readToTmp(2)
				return (((b and 0x1F).shl(16) or i(tmp[0]).shl(8) or i(tmp[1])) + 16512).toLong()
			}
			b and 0xF0 == 0xE0 -> {
				readToTmp(3)
				return (((b and 0x0F).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 2113664).toLong()
			}
			b and 0xF8 == 0xF0 -> {
				readToTmp(3)
				return (((b and 0x07).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 270549120).toLong()
			}
			b and 0xFC == 0xF8 -> {
				readToTmp(3)
				return (((b and 0x03).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) + 404766848).toLong()
			}
			b and 0xFE == 0xFC -> {
				readToTmp(3)
				return (((b and 0x01 or 0xFE).shl(24) or i(tmp[0]).shl(16) or i(tmp[1]).shl(8) or i(tmp[2])) - 1).toLong()
			}
			else               ->
				return readRawLong()
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
				byte = i(readByte())
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
		buffer.readArray(tmp, 0, size)
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

