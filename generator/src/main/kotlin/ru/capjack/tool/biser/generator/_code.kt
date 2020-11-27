package ru.capjack.tool.biser.generator

import java.io.StringWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class CodePath(val value: String) {
	
	val name: String
		by lazy {
			val path = value.split('.')
			val namePath = path.takeLastWhile { it[0].isUpperCase() }
			if (namePath.isEmpty()) path.last() else namePath.joinToString(".")
		}
	
	val parent: CodePath?
		by lazy { if (value == name) null else CodePath(value.dropLast(name.length + 1)) }
	
	fun asString(separator: Char): String {
		return if (separator == '.') value else value.replace('.', separator)
	}
	
	override fun toString(): String {
		return value
	}
	
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CodePath) return false
		if (value != other.value) return false
		return true
	}
	
	override fun hashCode(): Int {
		return value.hashCode()
	}
	
	fun resolve(name: String): CodePath {
		return CodePath("$value.$name")
	}
	
	fun resolve(name: CodePath): CodePath {
		return CodePath("$value.${name.value}")
	}
}

open class CodeFile : CodeBlock() {
	open fun write(path: Path) {
		Files.createDirectories(path.parent)
		Files.newBufferedWriter(path, Charsets.UTF_8).use {
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
	
	inline fun line(v: StringBuilder.() -> Unit) {
		line(StringBuilder().apply(v).toString())
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
	
	fun identLine(v: String) {
		append(CodeLine("\t".repeat(ident + 1) + v))
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
