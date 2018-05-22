package ru.capjack.biser

typealias Decoder<T> = (reader: BiserReader) -> T