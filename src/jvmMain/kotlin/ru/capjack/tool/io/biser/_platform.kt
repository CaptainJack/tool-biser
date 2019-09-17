package ru.capjack.tool.io.biser

import kotlin.reflect.KClass

internal actual fun ByteArray.toUtf8String(): String {
	return toString(Charsets.UTF_8)
}

internal actual fun String.toUtf8ByteArray(): ByteArray {
	return toByteArray(Charsets.UTF_8)
}

internal actual fun <T : Enum<T>> KClass<T>.getEnumValue(ordinal: Int): T {
	return java.enumConstants[ordinal]
}