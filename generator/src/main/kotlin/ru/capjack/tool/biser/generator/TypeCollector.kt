package ru.capjack.tool.biser.generator

import ru.capjack.tool.biser.generator.model.Type

interface TypeCollector {
	fun add(type: Type)
}