package ru.capjack.biser


class ByteArrayBiserInputStream(private var array: ByteArray = ByteArray(0)) : BiserInputStream {
	private var cursor: Int = 0
	
	override fun read(): Byte {
		return array[cursor++]
	}
	
	override fun read(target: ByteArray, len: Int) {
		PlatformUtils.copyByteArray(array, cursor, target, 0, len)
		cursor += len
	}
	
	fun reset() {
		cursor = 0
	}
	
	fun reset(array: ByteArray) {
		this.array = array
		cursor = 0
	}
}
