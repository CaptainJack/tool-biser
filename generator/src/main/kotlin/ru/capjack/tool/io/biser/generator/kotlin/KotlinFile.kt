package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodeBlock
import ru.capjack.tool.io.biser.generator.CodeFile
import ru.capjack.tool.io.biser.generator.ImportsCollection
import java.io.Writer

class KotlinFile(private val pack: String) : CodeFile(),
	ImportsCollection {
	private val packagePrefix = "$pack."
	
	private val imports = mutableSetOf<String>()
	
	override fun addImport(name: String) {
		if (!name.startsWith(packagePrefix)) {
			imports.add(name)
		}
	}
	
	override fun write(writer: Writer) {
		prepend(CodeBlock(0).apply {
			line("package $pack")
			
			if (imports.isNotEmpty()) {
				line()
				imports.forEach { line("import $it") }
			}
			
			line()
		})
		
		super.write(writer)
	}
}