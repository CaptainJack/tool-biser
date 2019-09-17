package ru.capjack.tool.io.biser

import kotlin.reflect.KClass

internal expect fun ByteArray.toUtf8String(): String

internal expect fun String.toUtf8ByteArray(): ByteArray

internal expect fun <T : Enum<T>> KClass<T>.getEnumValue(ordinal: Int): T