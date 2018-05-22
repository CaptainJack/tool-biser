package ru.capjack.biser

import kotlin.reflect.KClass

expect object PlatformUtils {
	fun encodeStringUtf8(value: String): ByteArray
	
	fun decodeStringUtf8(value: ByteArray): String
	
	fun <E : Enum<E>> getEnumValue(type: KClass<E>, ordinal: Int): E
	
	fun copyByteArray(src: ByteArray, srcPos: Int, dest: ByteArray, destPos: Int, length: Int)
	
	fun isEnumType(type: KClass<out Any>): Boolean
}
