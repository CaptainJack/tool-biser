package ru.capjack.biser

import kotlin.reflect.KClass

internal actual fun String.toUtf8ByteArray(): ByteArray {
	// https://gist.github.com/pascaldekloe/62546103a1576803dade9269ccf76330
	
	val l = this.length
	val bytes = ByteArray(l * 4)
	var i = 0
	var p = 0
	
	while (p != l) {
		var c: Int = this.charCodeAt(p)
		if (c < 128) {
			bytes[i++] = c.toByte()
		}
		else {
			if (c < 2048) {
				bytes[i++] = (c shr 6 or 192).toByte()
			}
			else {
				@Suppress("ConvertTwoComparisonsToRangeCheck")
				if (c > 0xd7ff && c < 0xdc00) {
					if (++p == l) {
						throw IllegalArgumentException("UTF-8 encode: incomplete surrogate pair")
					}
					val c2: Int = this.charCodeAt(p)
					
					if (c2 < 0xdc00 || c2 > 0xdfff) {
						throw IllegalArgumentException("UTF-8 encode: second char code 0x${c2.asDynamic().toString(16)} at index $p in surrogate pair out of range")
					}
					
					c = 0x10000 + (c and 0x03ff shl 10) + (c2 and 0x03ff)
					bytes[i++] = (c shr 18 or 240).toByte()
					bytes[i++] = (c shr 12 and 63 or 128).toByte()
				}
				else {
					bytes[i++] = (c shr 12 or 224).toByte()
				}
				bytes[i++] = (c shr 6 and 63 or 128).toByte()
			}
			bytes[i++] = (c and 63 or 128).toByte()
		}
		++p
	}
	
	return bytes.copyOf(i)
}

internal actual fun ByteArray.toUtf8String(): String {
	// https://gist.github.com/pascaldekloe/62546103a1576803dade9269ccf76330
	
	val l = this.size
	var s = ""
	var i = 0
	while (i < l) {
		var c = this[i++].toUint()
		if (c > 127) {
			when (c) {
				in 192..223 -> {
					if (i >= l) {
						throw IllegalArgumentException("UTF-8 decode: incomplete 2-byte sequence")
					}
					c = c.and(31).shl(6)
						.or(this[i].toUint().and(63))
				}
				in 224..239 -> {
					if (i + 1 >= l) {
						throw IllegalArgumentException("UTF-8 decode: incomplete 3-byte sequence")
					}
					c = c.and(15).shl(12)
						.or(this[i].toUint().and(63).shl(6))
						.or(this[++i].toUint().and(63))
				}
				in 240..247 -> {
					if (i + 2 >= l) {
						throw IllegalArgumentException("UTF-8 decode: incomplete 4-byte sequence")
					}
					c = c.and(7).shl(18)
						.or(this[i].toUint().and(63).shl(12))
						.or(this[++i].toUint().and(63).shl(6))
						.or(this[++i].toUint().and(63))
				}
				else        -> {
					throw IllegalArgumentException("UTF-8 decode: unknown multi byte start 0x${c.toString(16)} at index ${i - 1}")
				}
			}
			++i
		}
		
		when {
			c <= 0xffff   -> {
				s += c.asChar()
			}
			c <= 0x10ffff -> {
				c -= 0x10000
				s += c.shr(10).or(0xd800).asChar() + c.and(0x3FF).or(0xdc00).asChar()
			}
			else          -> {
				throw IllegalArgumentException("UTF-8 decode: code point 0x${c.toString(16)} exceeds UTF-16 reach")
			}
		}
	}
	return s
}

internal actual fun <T : Enum<T>> KClass<T>.getEnumValue(ordinal: Int): T {
	return js.asDynamic().values()[ordinal].unsafeCast<T>()
}

private fun String.charCodeAt(i: Int): Int {
	return this.asDynamic().charCodeAt(i).unsafeCast<Int>()
}

private fun Byte.toUint(): Int {
	return toInt().and(0xFF)
}

private fun Int.asChar(): String {
	return js("String").fromCharCode(this).unsafeCast<String>()
}
