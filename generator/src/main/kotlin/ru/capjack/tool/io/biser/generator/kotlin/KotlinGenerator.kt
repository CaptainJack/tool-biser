package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.Generator
import ru.capjack.tool.io.biser.generator.TypeAggregator
import ru.capjack.tool.io.biser.generator.model.Type
import ru.capjack.tool.io.biser.generator.model.TypeVisitor
import java.nio.file.Files
import java.nio.file.Path

class KotlinGenerator(
	private val targetPackage: String,
	private val structuresPackage: String = targetPackage,
	private val internal: Boolean = false,
	private val encodersName: String? = null,
	private val decodersName: String? = null
) : Generator() {
	
	override val encoderNameVisitor: TypeVisitor<String, Unit> = KotlinCoderNameVisitor(true)
	override val decoderNameVisitor: TypeVisitor<String, Unit> = KotlinCoderNameVisitor(false)
	override val writeCallVisitor: TypeVisitor<String, String> = KotlinWriteCallVisitor(encoderNameVisitor)
	override val readCallVisitor: TypeVisitor<String, Unit> = KotlinReadCallVisitor(decoderNameVisitor)
	
	private val typeNameVisitor = KotlinTypeNameVisitor()
	
	override fun generate(sourceDir: Path) {
		val packagePath = sourceDir.resolve(targetPackage.replace('.', '/'))
		Files.createDirectories(packagePath)
		
		val capitalizedPackageName = targetPackage.substringAfterLast('.').capitalize()
		
		generate(
			packagePath,
			encodersName ?: (capitalizedPackageName + "Encoders"),
			encoders,
			KotlinEncoderGeneratorVisitor(writeCallVisitor, encoderNameVisitor, typeNameVisitor, structuresPackage)
		)
		
		generate(
			packagePath,
			decodersName ?: (capitalizedPackageName + "Decoders"),
			decoders,
			KotlinDecoderGeneratorVisitor(readCallVisitor, decoderNameVisitor, typeNameVisitor, structuresPackage)
		)
		
	}
	
	private fun generate(packagePath: Path, name: String, types: Set<Type>, generator: TypeVisitor<Unit, KotlinGeneratorContext>) {
		val filePath = packagePath.resolve("$name.kt")
		val file = KotlinFile(targetPackage)
		val aggregator = TypeAggregator()
		val code = file.identBracketsCurly((if (internal) "internal " else "") + "object $name")
		val context = KotlinGeneratorContext(aggregator, code, file)
		
		types.forEach { aggregator.add(it) }
		
		while (true) {
			val type = aggregator.next() ?: break
			type.accept(generator, context)
		}
		
		file.write(filePath)
	}
}


