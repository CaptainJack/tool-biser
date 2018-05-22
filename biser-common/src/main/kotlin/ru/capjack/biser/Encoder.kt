package ru.capjack.biser

typealias Encoder<T> = (writer: BiserWriter, value: T) -> Unit