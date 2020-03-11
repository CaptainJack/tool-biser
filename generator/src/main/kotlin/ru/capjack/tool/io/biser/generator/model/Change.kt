package ru.capjack.tool.io.biser.generator.model

enum class Change {
	ABSENT,
	COMPATIBLY,
	FULL;
	
	fun raiseTo(change: Change): Change {
		return if (change.ordinal > ordinal) change else change
	}
}