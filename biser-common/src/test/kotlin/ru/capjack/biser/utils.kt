package ru.capjack.biser

import kotlin.test.assertEquals

fun bytes(vararg b: Int): ByteArray {
	return b.map { it.toByte() }.toByteArray()
}

fun assertByteArrayEquals(expected: ByteArray, actual: ByteArray) {
	assertEquals(expected.size, actual.size, "Size)")
	repeat(expected.size) {
		assertEquals(expected[it], actual[it], "Byte at $it")
	}
}
