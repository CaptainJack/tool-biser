package ru.capjack.tool.biser

import kotlin.test.assertEquals

fun bytes(vararg b: Int): ByteArray {
	return b.map { it.toByte() }.toByteArray()
}

fun assertPrimitiveArrayEquals(expected: BooleanArray, actual: BooleanArray) {
	assertEquals(expected.size, actual.size, "Size")
	assertEquals(expected.joinToString { it.hex() }, actual.joinToString { it.hex() })
}

fun assertPrimitiveArrayEquals(expected: ByteArray, actual: ByteArray) {
	assertEquals(expected.size, actual.size, "Size")
	assertEquals(expected.joinToString { it.hex() }, actual.joinToString { it.hex() })
}

fun assertPrimitiveArrayEquals(expected: IntArray, actual: IntArray) {
	assertEquals(expected.size, actual.size, "Size")
	assertEquals(expected.joinToString { it.hex() }, actual.joinToString { it.hex() })
}

fun assertPrimitiveArrayEquals(expected: LongArray, actual: LongArray) {
	assertEquals(expected.size, actual.size, "Size")
	assertEquals(expected.joinToString { it.hex() }, actual.joinToString { it.hex() })
}

fun assertPrimitiveArrayEquals(expected: DoubleArray, actual: DoubleArray) {
	assertEquals(expected.size, actual.size, "Size")
	assertEquals(expected.joinToString { it.hex() }, actual.joinToString { it.hex() })
}


fun Boolean.hex(): String {
	return if (this) "01" else "00"
}

fun Number.hex(): String {
	return toInt().and(0xFF).toString(16).padStart(2, '0')
}