package ru.capjack.tool.biser

open class BiserException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

open class BiserReadException(message: String, cause: Throwable? = null) : BiserException(message, cause)

open class BiserReadNegativeSizeException(size: Int) : BiserReadException("Negative size ($size)")