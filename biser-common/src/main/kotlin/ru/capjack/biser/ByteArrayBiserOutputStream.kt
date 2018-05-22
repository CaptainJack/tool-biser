package ru.capjack.biser

class ByteArrayBiserOutputStream(capacity: Int = 32) : BiserOutputStream {
	private var array = ByteArray(capacity)
	private var cursor: Int = 0
	
	override fun write(byte: Byte) {
		increaseCapacity(1)
		array[cursor++] = byte
	}
	
	override fun write(bytes: ByteArray, len: Int) {
		increaseCapacity(len)
		PlatformUtils.copyByteArray(bytes, 0, array, cursor, len)
		cursor += len
	}
	
	fun clear() {
		cursor = 0
	}
	
	fun toArray(): ByteArray {
		return array.copyOf(cursor)
	}
	
	private fun increaseCapacity(v: Int) {
		val newCapacity = cursor + v
		var capacity = array.size
		
		if (newCapacity < 0) {
			throw RuntimeException()
		}
		
		if (capacity < newCapacity) {
			capacity += capacity shr 1
			if (capacity < newCapacity) {
				capacity = newCapacity
			} else if (capacity < 0) {
				capacity = Int.MAX_VALUE
			}
			array = array.copyOf(capacity)
		}
	}
}