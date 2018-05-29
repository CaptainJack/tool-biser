package ru.capjack.biser

interface BiserInputStream {
	fun read(): Byte
	
	fun read(target: ByteArray, len: Int = target.size)
}