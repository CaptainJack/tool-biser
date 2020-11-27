@file:Suppress("NOTHING_TO_INLINE")

package ru.capjack.tool.biser

internal actual inline fun String.charCodeAt(index: Int): Int {
	return this[index].toInt()
}

internal actual inline fun ByteArray.decodeToUtf8String(offset: Int, length: Int): String {
	return String(this, offset, length, Charsets.UTF_8)
}