package com.soywiz.korma.random

import java.util.*

fun <T> List<T>.random(random: Random = Random()): T {
	if (this.isEmpty()) throw IllegalArgumentException("Empty list")
	return this[random.nextInt(this.size)]
}