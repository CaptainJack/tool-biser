package ru.capjack.biser

fun Byte.toUint(): Int {
	return toInt() and 0xFF
}