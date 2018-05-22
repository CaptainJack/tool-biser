package ru.capjack.biser

interface BiserOutputStream {
	fun write(byte: Byte)
	
	fun write(bytes: ByteArray, len: Int = bytes.size)
}
