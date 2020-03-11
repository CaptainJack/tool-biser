package ru.capjack.tool.io.biser.generator

import ru.capjack.tool.io.biser.generator.model.Type

class TypeAggregator : TypeCollector {
	private val passedTypes = mutableSetOf<Type>()
	private val remainedTypes = mutableSetOf<Type>()
	
	override fun add(type: Type) {
		if (!passedTypes.contains(type)) {
			remainedTypes.add(type)
		}
	}
	
	fun next(): Type? {
		return remainedTypes.firstOrNull()?.also {
			remainedTypes.remove(it)
			passedTypes.add(it)
		}
	}
}