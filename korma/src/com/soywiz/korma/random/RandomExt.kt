package com.soywiz.korma.random

import com.soywiz.korma.geom.Point2d
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.interpolation.Interpolable
import com.soywiz.korma.interpolation.interpolate
import com.soywiz.korma.interpolation.interpolateAny
import java.util.*

fun <T> List<T>.getCyclic(index: Int) = this[index % this.size]

fun <T> List<T>.random(random: Random = Random()): T {
	if (this.isEmpty()) throw IllegalArgumentException("Empty list")
	return this[random.nextInt(this.size)]
}

operator fun Random.get(min: Double, max: Double): Double = min + nextDouble() * (max - min)
operator fun Random.get(min: Int, max: Int): Int = min + nextInt(max - min)
operator fun Random.get(range: IntRange): Int = range.start + this.nextInt(range.endInclusive - range.start + 1)
operator fun Random.get(range: LongRange): Long = range.start + this.nextLong() % (range.endInclusive - range.start + 1)

operator fun <T> Random.get(list: List<T>): T = list[this[list.indices]]

operator fun Random.get(rectangle: Rectangle): Point2d = Point2d(this[rectangle.left, rectangle.right], this[rectangle.top, rectangle.bottom])

operator fun <T : Interpolable<T>> Random.get(l: T, r: T): T = (this.nextInt(0x10001).toDouble() / 0x10000.toDouble()).interpolate(l, r)

operator fun <T : Comparable<T>> Random.get(range: ClosedRange<T>): T {
	return interpolateAny(range.start, range.endInclusive, (this.nextInt(0x10001).toDouble() / 0x10000.toDouble()))
}

operator fun <T> Random.get(weighted: Map<T, Int>): T {
	val totalWeight = weighted.values.sum()
	val found = this[0, totalWeight + 1]

	var offset = 0
	for ((key, len) in weighted.entries) {
		if (found in (offset until (offset + len))) return key
		offset += len
	}
	return weighted.keys.first()
}