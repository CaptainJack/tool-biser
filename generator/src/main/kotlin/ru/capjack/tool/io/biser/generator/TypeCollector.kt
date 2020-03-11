package ru.capjack.tool.io.biser.generator

import ru.capjack.tool.io.biser.generator.model.Type

interface TypeCollector {
	fun add(type: Type)
}