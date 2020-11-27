package ru.capjack.tool.biser.generator.kotlin

import ru.capjack.tool.biser.generator.CodePath
import ru.capjack.tool.biser.generator.ImportsCollection
import ru.capjack.tool.biser.generator.model.TypeVisitor

interface KotlinGearFactory {
	fun createTypeNameVisitor(targetPackage: CodePath): TypeVisitor<String, ImportsCollection>
	fun createWriteCallVisitor(innerEncoderNameVisitor: TypeVisitor<String, Unit>): TypeVisitor<String, String>
	fun createReadCallVisitor(innerDecodeNameVisitor: TypeVisitor<String, Unit>): TypeVisitor<String, Unit>
}

open class DefaultKotlinGearFactory : KotlinGearFactory {
	override fun createTypeNameVisitor(targetPackage: CodePath): TypeVisitor<String, ImportsCollection> {
		return KotlinTypeNameVisitor(targetPackage)
	}
	
	override fun createWriteCallVisitor(innerEncoderNameVisitor: TypeVisitor<String, Unit>): TypeVisitor<String, String> {
		return KotlinWriteCallVisitor(innerEncoderNameVisitor)
	}
	
	override fun createReadCallVisitor(innerDecodeNameVisitor: TypeVisitor<String, Unit>): TypeVisitor<String, Unit> {
		return KotlinReadCallVisitor(innerDecodeNameVisitor)
	}
}

