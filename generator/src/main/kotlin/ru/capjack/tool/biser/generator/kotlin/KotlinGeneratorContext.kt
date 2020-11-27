package ru.capjack.tool.biser.generator.kotlin

import ru.capjack.tool.biser.generator.CodeBlock
import ru.capjack.tool.biser.generator.ImportsCollection
import ru.capjack.tool.biser.generator.TypeCollector

open class KotlinGeneratorContext(
	val types: TypeCollector,
	val code: CodeBlock,
	val imports: ImportsCollection
)