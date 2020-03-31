package ru.capjack.tool.io.biser.generator

interface CoderNameScopeVisitor {
	fun visitPrimitiveScope(name: String): String
	
	fun visitGeneratedScope(name: String): String
}