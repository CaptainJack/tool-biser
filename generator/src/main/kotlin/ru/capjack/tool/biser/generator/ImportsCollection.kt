package ru.capjack.tool.biser.generator

interface ImportsCollection {
	fun addImport(name: String)
	
	fun addImport(name: CodePath)
}