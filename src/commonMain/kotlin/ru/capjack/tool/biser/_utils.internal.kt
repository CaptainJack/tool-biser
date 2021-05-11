@file:Suppress("NOTHING_TO_INLINE")

package ru.capjack.tool.biser

const val x00 = 0x00.toByte()
const val x01 = 0x01.toByte()
const val xFE = 0xFE.toByte()
const val xFF = 0xFF.toByte()

internal fun i(byte: Byte): Int {
	return byte.toInt().and(0xFF)
}

internal inline operator fun ByteArray.set(index: Int, value: Int) {
	set(index, value.toByte())
}

internal inline operator fun ByteArray.set(index: Int, value: Long) {
	set(index, value.toByte())
}

internal fun String.encodeToUtf8ByteArray(target: ByteArray, offset: Int): Int {
	val l = length
	var i = 0
	var t = offset
	
	while (i != l) {
		var c = this[i++].code
		if (c < 0x80) {
			target[t++] = c
		}
		else if (c < 0x0800) {
			target[t++] = 0xC0 or (c shr 6)
			target[t++] = 0x80 or (c and 0x3F)
		}
		else if (c < 0xD800 || c >= 0xE000) {
			target[t++] = 0xE0 or (c shr 12)
			target[t++] = 0x80 or (c shr 6 and 0x3F)
			target[t++] = 0x80 or (c and 0x3F)
		}
		else {
			c = 0x10000 + ((c and 0x03FF shl 10) or (this[i++].code and 0x03FF))
			target[t++] = 0xF0 or (c shr 18)
			target[t++] = 0x80 or (c shr 12 and 0x3F)
			target[t++] = 0x80 or (c shr 6 and 0x3F)
			target[t++] = 0x80 or (c and 0x3F)
		}
	}
	
	return t - offset
}
