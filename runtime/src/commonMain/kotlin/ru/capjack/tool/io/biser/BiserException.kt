package ru.capjack.tool.io.biser

import kotlin.reflect.KClass

open class BiserException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

open class EncoderException(message: String, cause: Throwable? = null) : BiserException(message, cause)

open class DecoderException(message: String, cause: Throwable? = null) : BiserException(message, cause)

class UnknownEntityEncoderException(entity: Any) : EncoderException("Entity ${entity::class} is unknown for encode")

class UnknownIdDecoderException(id: Int, type: KClass<*>) : EncoderException("Id $id is unknown for decode ${type}")