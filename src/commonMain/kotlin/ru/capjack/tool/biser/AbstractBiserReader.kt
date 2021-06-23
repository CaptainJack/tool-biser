package ru.capjack.tool.biser

import ru.capjack.tool.lang.EMPTY_BOOLEAN_ARRAY
import ru.capjack.tool.lang.EMPTY_DOUBlE_ARRAY
import ru.capjack.tool.lang.EMPTY_INT_ARRAY
import ru.capjack.tool.lang.EMPTY_LONG_ARRAY
import ru.capjack.tool.lang.toHexString
import ru.capjack.tool.utils.collections.BooleanArrayList
import ru.capjack.tool.utils.collections.ByteArrayList
import ru.capjack.tool.utils.collections.DoubleArrayList
import ru.capjack.tool.utils.collections.IntArrayList
import ru.capjack.tool.utils.collections.LongArrayList

abstract class AbstractBiserReader : BiserReader {
	protected val memory = ByteArray(8)
	
	override fun readBoolean(): Boolean {
		return when (val b = readByte()) {
			x00  -> false
			x01  -> true
			else -> throw BiserReadException("Illegal boolean value (0x${b.toHexString()})")
		}
	}
	
	override fun readInt(): Int {
		val b = i(readByte())
		
		when {
			b and 0x80 == 0x00 ->
				return b
			b == 0xFF          ->
				return -1
			b and 0xC0 == 0x80 ->
				return ((b and 0x3F).shl(8) or i(readByte())) + 128
			b and 0xE0 == 0xC0 -> {
				readToMemory(2)
				return ((b and 0x1F).shl(16) or i(memory[0]).shl(8) or i(memory[1])) + 16512
			}
			b and 0xF0 == 0xE0 -> {
				readToMemory(3)
				return ((b and 0x0F).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 2113664
			}
			b and 0xF8 == 0xF0 -> {
				readToMemory(3)
				return ((b and 0x07).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 270549120
			}
			b and 0xFC == 0xF8 -> {
				readToMemory(3)
				return ((b and 0x03).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 404766848
			}
			b and 0xFE == 0xFC -> {
				readToMemory(3)
				return ((b and 0x01 or 0xFE).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) - 1
			}
			b == 0xFE          -> {
				readToMemory(4)
				return (i(memory[0]) shl 24)
					.or(i(memory[1]) shl 16)
					.or(i(memory[2]) shl 8)
					.or(i(memory[3]))
			}
			else               ->
				throw BiserReadException("Illegal int value (0x${b.toHexString()})")
		}
	}
	
	override fun readLong(): Long {
		val b = i(readByte())
		
		when {
			b and 0x80 == 0x00 ->
				return b.toLong()
			b == 0xFF          ->
				return -1
			b and 0xC0 == 0x80 ->
				return (((b and 0x3F).shl(8) or i(readByte())) + 128).toLong()
			b and 0xE0 == 0xC0 -> {
				readToMemory(2)
				return (((b and 0x1F).shl(16) or i(memory[0]).shl(8) or i(memory[1])) + 16512).toLong()
			}
			b and 0xF0 == 0xE0 -> {
				readToMemory(3)
				return (((b and 0x0F).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 2113664).toLong()
			}
			b and 0xF8 == 0xF0 -> {
				readToMemory(3)
				return (((b and 0x07).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 270549120).toLong()
			}
			b and 0xFC == 0xF8 -> {
				readToMemory(3)
				return (((b and 0x03).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) + 404766848).toLong()
			}
			b and 0xFE == 0xFC -> {
				readToMemory(3)
				return (((b and 0x01 or 0xFE).shl(24) or i(memory[0]).shl(16) or i(memory[1]).shl(8) or i(memory[2])) - 1).toLong()
			}
			b == 0xFE          ->
				return readLongRaw()
			else               ->
				throw BiserReadException("Illegal long value (0x${b.toHexString()})")
		}
	}
	
	override fun readDouble(): Double {
		return Double.fromBits(readLongRaw())
	}
	
	override fun readBooleanArray(): BooleanArray {
		return readBooleanArray(readInt())
	}
	
	override fun readByteArray(): ByteArray {
		return readByteArray(readInt())
	}
	
	override fun readIntArray(): IntArray {
		return readIntArray(readInt())
	}
	
	override fun readLongArray(): LongArray {
		return readLongArray(readInt())
	}
	
	override fun readDoubleArray(): DoubleArray {
		return readDoubleArray(readInt())
	}
	
	@Suppress("UNCHECKED_CAST")
	override fun <E> readList(decoder: Decoder<E>): List<E> {
		val size = readInt()
		
		if (size == 0)
			return emptyList()
		
		if (decoder === Decoders.BOOLEAN)
			return BooleanArrayList(readBooleanArray(size)) as List<E>
		if (decoder === Decoders.BYTE)
			return ByteArrayList(readByteArray(size)) as List<E>
		if (decoder === Decoders.INT)
			return IntArrayList(readIntArray(size)) as List<E>
		if (decoder === Decoders.LONG)
			return LongArrayList(readLongArray(size)) as List<E>
		if (decoder === Decoders.DOUBLE)
			return DoubleArrayList(readDoubleArray(size)) as List<E>
		
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		return List(size) { decoder(this) }
	}
	
	override fun <K, V> readMap(keyDecoder: Decoder<K>, valueDecoder: Decoder<V>): Map<K, V> {
		val size = readInt()
		
		if (size == 0)
			return emptyMap()
		
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		val map = HashMap<K, V>(size)
		
		repeat(size) {
			map[keyDecoder()] = valueDecoder()
		}
		
		return map
	}
	
	override fun <E> read(decoder: Decoder<E>): E {
		return decoder(this)
	}
	
	protected abstract fun readToMemory(size: Int)
	
	protected abstract fun readByteArray(size: Int): ByteArray
	
	private fun readBooleanArray(size: Int): BooleanArray {
		if (size == 0)
			return EMPTY_BOOLEAN_ARRAY
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		var bit = 7
		var byte = 0
		
		return BooleanArray(size) {
			if (++bit == 8) {
				bit = 0
				byte = i(readByte())
			}
			val m = 1 shl bit
			byte and m == m
		}
	}
	
	private fun readIntArray(size: Int): IntArray {
		if (size == 0)
			return EMPTY_INT_ARRAY
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		return IntArray(size) { readInt() }
	}
	
	private fun readLongArray(size: Int): LongArray {
		if (size == 0)
			return EMPTY_LONG_ARRAY
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		return LongArray(size) { readLong() }
	}
	
	private fun readDoubleArray(size: Int): DoubleArray {
		if (size == 0)
			return EMPTY_DOUBlE_ARRAY
		if (size < 0)
			throw BiserReadNegativeSizeException(size)
		
		return DoubleArray(size) { readDouble() }
	}
	
	private fun readLongRaw(): Long {
		readToMemory(8)
		
		return (memory[0].toLong() and 0xFF shl 56)
			.or(memory[1].toLong() and 0xFF shl 48)
			.or(memory[2].toLong() and 0xFF shl 40)
			.or(memory[3].toLong() and 0xFF shl 32)
			.or(memory[4].toLong() and 0xFF shl 24)
			.or(memory[5].toLong() and 0xFF shl 16)
			.or(memory[6].toLong() and 0xFF shl 8)
			.or(memory[7].toLong() and 0xFF)
	}
}