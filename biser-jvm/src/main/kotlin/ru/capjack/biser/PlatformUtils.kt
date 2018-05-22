package ru.capjack.biser

import kotlin.reflect.KClass

actual object PlatformUtils {
	actual fun encodeStringUtf8(value: String): ByteArray {
		return value.toByteArray(Charsets.UTF_8)
	}
	
	actual fun decodeStringUtf8(value: ByteArray): String {
		return value.toString(Charsets.UTF_8)
	}
	
	actual fun <E : Enum<E>> getEnumValue(type: KClass<E>, ordinal: Int): E {
		return type.java.enumConstants.first { it.ordinal == ordinal }
	}
	
	actual fun copyByteArray(src: ByteArray, srcPos: Int, dest: ByteArray, destPos: Int, length: Int) {
		System.arraycopy(src, srcPos, dest, destPos, length)
	}
	
	actual fun isEnumType(type: KClass<out Any>): Boolean {
		return type.java.isEnum
	}
}