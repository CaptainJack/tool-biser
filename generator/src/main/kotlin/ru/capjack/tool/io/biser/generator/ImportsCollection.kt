package ru.capjack.tool.io.biser.generator

interface ImportsCollection {
	fun addImport(name: String)
	
	fun addImport(name: CodePath)
}