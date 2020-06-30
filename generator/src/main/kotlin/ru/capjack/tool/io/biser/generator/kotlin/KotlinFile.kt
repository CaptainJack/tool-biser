package ru.capjack.tool.io.biser.generator.kotlin

import ru.capjack.tool.io.biser.generator.CodeBlock
import ru.capjack.tool.io.biser.generator.CodeFile
import ru.capjack.tool.io.biser.generator.CodePath
import ru.capjack.tool.io.biser.generator.ImportsCollection
import java.io.Writer
import java.nio.file.Path

class KotlinFile(private val name: CodePath) : CodeFile(), ImportsCollection {
	private val imports = mutableSetOf<CodePath>()
	
	override fun addImport(name: String) {
		addImport(CodePath(name))
	}
	
	override fun addImport(name: CodePath) {
		if (this.name.parent != name.parent) {
			imports.add(name)
		}
	}
	
	override fun write(writer: Writer) {
		prepend(CodeBlock(0).apply {
			val pack = name.parent
			if (pack != null) {
				line("package ${pack.value}")
			}
			
			val allImports = imports.map { it.value }.sorted()
			val imports = mutableListOf<String>()
			
			allImports.forEach { path ->
				if (imports.none { path.startsWith("$it.") }) {
					imports.add(path)
				}
			}
			
			if (imports.isNotEmpty()) {
				line()
				imports.forEach { line("import $it") }
			}
			
			line()
		})
		
		super.write(writer)
	}
	
	override fun write(path: Path) {
		super.write(path.resolve(name.asString('/') + ".kt"))
	}
}