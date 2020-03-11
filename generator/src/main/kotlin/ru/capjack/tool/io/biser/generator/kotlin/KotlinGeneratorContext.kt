package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodeBlock
import ru.capjack.tool.io.biser.generator.ImportsCollection
import ru.capjack.tool.io.biser.generator.TypeCollector

open class KotlinGeneratorContext(
	val types: TypeCollector,
	val code: CodeBlock,
	val imports: ImportsCollection
)