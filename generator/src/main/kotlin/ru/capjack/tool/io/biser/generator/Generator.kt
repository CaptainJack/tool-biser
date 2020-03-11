package ru.capjack.tool.io.biser.generator

import ru.capjack.tool.io.biser.generator.model.Type
import ru.capjack.tool.io.biser.generator.model.TypeVisitor
import java.nio.file.Path

abstract class Generator {
	protected abstract val encoderNameVisitor: TypeVisitor<String, Unit>
	protected abstract val decoderNameVisitor: TypeVisitor<String, Unit>
	protected abstract val writeCallVisitor: TypeVisitor<String, String>
	protected abstract val readCallVisitor: TypeVisitor<String, Unit>
	
	protected val encoders = mutableSetOf<Type>()
	protected val decoders = mutableSetOf<Type>()
	
	fun registerEncoder(type: Type) {
		encoders.add(type)
	}
	
	fun registerDecoder(type: Type) {
		decoders.add(type)
	}
	
	fun provideWriteCall(type: Type, value: String): String {
		registerEncoder(type)
		return type.accept(writeCallVisitor, value)
	}
	
	fun provideReadCall(type: Type): String {
		registerDecoder(type)
		return type.accept(readCallVisitor)
	}
	
	abstract fun generate(sourceDir: Path)
}


