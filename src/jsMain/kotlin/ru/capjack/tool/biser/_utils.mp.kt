@file:Suppress("NOTHING_TO_INLINE")

package ru.capjack.tool.biser

internal actual fun ByteArray.decodeToUtf8String(offset: Int, length: Int): String {
	val l = offset + length
	var i = offset
	var s = ""
	
	while (i < l) {
		var c = i(this[i++])
		when {
			c < 0x80        -> {
				s += c.toChar()
			}
			c in 0xC0..0xDF -> {
				s += (c and 0x1F shl 6)
					.or(i(this[i++]) and 0x3F)
					.toChar()
			}
			c in 0xE0..0xEF -> {
				s += (c and 0x0F shl 12)
					.or(i(this[i++]) and 0x3F shl 6)
					.or(i(this[i++]) and 0x3F)
					.toChar()
			}
			else            -> {
				c = (c and 0x07 shl 18)
					.or(i(this[i++]) and 0x3F shl 12)
					.or(i(this[i++]) and 0x3F shl 6)
					.or(i(this[i++]) and 0x3F)
					.minus(0x10000)
				
				s += (0xD800 or (c shr 10)).toChar()
				s += (0xDC00 or (c and 0x03FF)).toChar()
			}
		}
	}
	
	return s
}


