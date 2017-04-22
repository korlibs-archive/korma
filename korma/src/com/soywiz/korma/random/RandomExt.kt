package com.soywiz.korma.random

import java.util.*

fun <T> List<T>.random(random: Random = Random()): T {
	if (this.isEmpty()) throw IllegalArgumentException("Empty list")
	return this[random.nextInt(this.size)]
}

operator fun Random.get(min: Double, max: Double): Double = min + nextDouble() * (max - min)
operator fun Random.get(min: Int, max: Int): Int = min + nextInt(max - min)
operator fun Random.get(range: IntRange): Int = range.start + this.nextInt(range.endInclusive - range.start + 1)
operator fun Random.get(range: LongRange): Long = range.start + this.nextLong() % (range.endInclusive - range.start + 1)