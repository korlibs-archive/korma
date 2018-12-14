package com.soywiz.korma.random

import com.soywiz.korma.Vector2
import com.soywiz.korma.geom.Point2d
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.interpolation.Interpolable
import com.soywiz.korma.interpolation.interpolate
import com.soywiz.korma.interpolation.interpolateAny
import com.soywiz.korma.math.*
import kotlin.random.*

fun Random.intStream(): Sequence<Int> = sequence { while (true) yield(nextInt()) }
fun Random.intStream(from: Int, until: Int): Sequence<Int> = sequence { while (true) yield(nextInt(from, until)) }
fun Random.intStream(range: IntRange): Sequence<Int> = intStream(range.start, range.endInclusive + 1)

fun Random.doubleStream(): Sequence<Double> = sequence { while (true) yield(nextDouble()) }

fun <T> List<T>.getCyclic(index: Int) = this[index % this.size]

fun <T> List<T>.random(random: Random = MtRandom()): T {
    if (this.isEmpty()) throw IllegalArgumentException("Empty list")
    return this[random.nextInt(this.size)]
}

//fun Random.nextDoubleFixed(): Double {
//    val value = nextBits(31)
//    val rvalue = if (value == Int.MAX_VALUE) value - 1 else value
//    return value.toDouble() / Int.MAX_VALUE.toDouble()
//}

operator fun Random.get(min: Double, max: Double): Double = min + nextDouble() * (max - min)
operator fun Random.get(min: Int, max: Int): Int = min + nextInt(max - min)
operator fun Random.get(range: IntRange): Int = range.start + this.nextInt(range.endInclusive - range.start + 1)
operator fun Random.get(range: LongRange): Long = range.start + this.nextLong() % (range.endInclusive - range.start + 1)

operator fun <T> Random.get(list: List<T>): T = list[this[list.indices]]

operator fun Random.get(rectangle: Rectangle): Point2d =
    Vector2(this[rectangle.left, rectangle.right], this[rectangle.top, rectangle.bottom])

operator fun <T : Interpolable<T>> Random.get(l: T, r: T): T =
    (this.nextInt(0x10001).toDouble() / 0x10000.toDouble()).interpolate(l, r)

operator fun <T : Comparable<T>> Random.get(range: ClosedRange<T>): T =
    interpolateAny(range.start, range.endInclusive, (this.nextInt(0x10001).toDouble() / 0x10000.toDouble()))


fun <T> Random.weighted(weights: Map<T, Double>): T = shuffledWeighted(weights).first()
fun <T> Random.weighted(weights: Weights<T>): T = shuffledWeighted(weights).first()

fun <T> Random.shuffledWeighted(weights: Map<T, Double>): List<T> = shuffledWeighted(Weights(weights))
fun <T> Random.shuffledWeighted(values: List<T>, weights: List<Double>): List<T> = shuffledWeighted(Weights(values, weights))
fun <T> Random.shuffledWeighted(weights: Weights<T>): List<T> {
    val randoms = (0 until weights.items.size).map { -pow(nextDouble(), (1.0 / weights.normalizedWeights[it])) }
    val sortedIndices = (0 until weights.items.size).sortedWith(Comparator { a, b -> randoms[a].compareTo(randoms[b]) })
    return sortedIndices.map { weights.items[it] }
}

data class Weights<T>(val weightsMap: Map<T, Double>) {
    constructor(vararg pairs: Pair<T, Double>) : this(mapOf(*pairs))
    constructor(values: List<T>, weights: List<Double>) : this(values.zip(weights).toMap())

    val items = weightsMap.keys.toList()
    val weights = weightsMap.values.toList()
    val normalizedWeights = normalizeWeights(weights)

    companion object {
        private fun normalizeWeights(weights: List<Double>): List<Double> {
            val min = weights.min() ?: 0.0
            return weights.map { (it + min) + 1 }
        }
    }
}
