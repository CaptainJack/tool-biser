package ru.capjack.tool.biser.generator

interface CoderNameScopeVisitor {
	fun visitPrimitiveScope(name: String): String
	
	fun visitGeneratedScope(name: String): String
}