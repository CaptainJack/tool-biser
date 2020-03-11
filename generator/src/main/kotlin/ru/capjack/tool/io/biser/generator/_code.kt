package ru.capjack.tool.io.biser.generator

import java.io.StringWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

open class CodeFile : CodeBlock() {
	open fun write(file: Path) {
		Files.newBufferedWriter(file, Charsets.UTF_8).use {
			write(it)
		}
	}
	
	fun write(): String {
		val writer = StringWriter()
		write(writer)
		return writer.toString()
	}
}

interface CodeStatement {
	fun write(writer: Writer)
}

class CodeLine(private val content: String) : CodeStatement {
	override fun write(writer: Writer) {
		writer.append(content).append('\n')
	}
}

open class CodeBlock(private val ident: Int = 0) : CodeStatement {
	private val statements = LinkedList<CodeStatement>()
	
	fun line(v: String) {
		append(CodeLine("\t".repeat(ident) + v))
	}
	
	fun line() {
		append(CodeLine("\t".repeat(ident)))
	}
	
	fun <T : CodeStatement> prepend(statement: T): T {
		statements.addFirst(statement)
		return statement
	}
	
	fun <T : CodeStatement> append(statement: T): T {
		statements.addLast(statement)
		return statement
	}
	
	fun ident(): CodeBlock {
		return append(CodeBlock(ident + 1))
	}
	
	inline fun ident(block: CodeBlock.() -> Unit) {
		ident().block()
	}
	
	fun identBracketsCurly(line: String): CodeBlock {
		line("$line{")
		val block = ident()
		line("}")
		return block
	}
	
	inline fun identBracketsCurly(line: String, block: CodeBlock.() -> Unit) {
		identBracketsCurly(line).block()
	}
	
	fun identBracketsRound(line: String): CodeBlock {
		line("$line(")
		val block = ident()
		line(")")
		return block
	}
	
	inline fun identBracketsRound(line: String, block: CodeBlock.() -> Unit) {
		identBracketsRound(line).block()
	}
	
	override fun write(writer: Writer) {
		statements.forEach { it.write(writer) }
	}
}
