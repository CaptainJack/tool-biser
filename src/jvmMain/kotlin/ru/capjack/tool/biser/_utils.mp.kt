@file:Suppress("NOTHING_TO_INLINE")

package ru.capjack.tool.biser

import java.nio.charset.StandardCharsets

internal actual inline fun ByteArray.decodeToUtf8String(offset: Int, length: Int): String {
	return java.lang.String(this, offset, length, StandardCharsets.UTF_8) as String
}