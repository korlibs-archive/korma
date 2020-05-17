package com.soywiz.korma.segment

import com.soywiz.kds.*
import com.soywiz.korma.annotations.*

/**
 * Non-overlapping SegmentSet
 */
@KormaExperimental
class IntSegmentSet {
    @PublishedApi
    internal val min = IntArrayList(16)
    @PublishedApi
    internal val max = IntArrayList(16)
    val size get() = min.size
    fun isEmpty() = size == 0
    fun isNotEmpty() = size > 0

    fun clear() = this.apply {
        min.clear()
        max.clear()
    }

    val minMin get() = if (isNotEmpty()) min.getAt(0) else 0
    val maxMax get() = if (isNotEmpty()) max.getAt(max.size - 1) else 0

    fun findNearIndex(x: Int): BSearchResult {
        return BSearchResult(genericBinarySearch(0, size) { v ->
            val min = this.min.getAt(v)
            val max = this.max.getAt(v)
            when {
                x < min -> +1
                x > max -> -1
                else -> 0
            }
        })
    }

    fun findNearMinIndex(x: Int): BSearchResult = BSearchResult(genericBinarySearch(0, size) { x.compareTo(this.min.getAt(it)) })
    fun findNearMaxIndex(x: Int): BSearchResult = BSearchResult(genericBinarySearch(0, size) { x.compareTo(this.max.getAt(it)) })

    fun add(min: Int, max: Int) = this.apply {
        check(min <= max)
        when {
            isEmpty() -> run { this.min.add(min) }.also { this.max.add(max) }
            min == maxMax -> this.max[this.max.size - 1] = max
            max == minMin -> this.min[0] = min
            else -> {
                //val res = findNearMinIndex(min).also { (if (it < 0) (-it - 1) else it).coerceAtLeast(0) }
                //TODO()
                var removeStart = 0
                var removeEnd = -1
                var removeCount = -1
                var n = 0

                // @TODO: Do this with a binary search findNearMinIndex and findNearMaxIndex
                fastForEach { x1, x2 ->
                    if (intersects(x1, x2, min, max)) {
                        if (removeStart == -1) removeStart = n
                        this.min[removeStart] = kotlin.math.min(this.min.getAt(removeStart), kotlin.math.min(x1, min))
                        this.max[removeStart] = kotlin.math.max(this.max.getAt(removeStart), kotlin.math.max(x2, max))
                        removeEnd = n
                        removeCount++
                    }
                    n++
                }

                when {
                    // Combined
                    removeCount > 0 -> {
                        this.min.removeAt(removeStart + 1, removeCount)
                    }
                    // Insert at the beginning
                    min < minMin -> {
                        this.min.insertAt(0, min)
                        this.max.insertAt(0, max)
                    }
                    // Insert at the end
                    max > maxMax -> {
                        this.min.add(min)
                        this.max.add(max)
                    }
                    // Insert at a place
                    else -> {
                        val index = findNearMaxIndex(min).nearIndex
                        for (n in (index - 1) until (index + 2)) {
                            if (n !in 0 until size) continue
                            val x2 = this.max.getAt(n)
                            if (min > x2) {
                                this.min.insertAt(n + 1, min)
                                this.max.insertAt(n + 1, max)
                                return@apply
                            }
                        }
                        error("Unexpected")
                    }
                }
            }
        }
    }

    //fun remove(min: Int, max: Int) = this.apply { TODO() }
    //fun intersect(min: Int, max: Int) = this.apply { TODO() }

    fun intersection(min: Int, max: Int): Pair<Int, Int>? {
        var out: Pair<Int, Int>? = null
        intersection(min, max) { x1, x2 -> out = x1 to x2 }
        return out
    }

    inline fun intersection(min: Int, max: Int, out: (min: Int, max: Int) -> Unit): Boolean {
        val size = this.size
        val nmin = findNearMinIndex(min).nearIndex - 1
        val nmax = findNearMaxIndex(max).nearIndex + 1

        for (n in nmin.coerceIn(0, size - 1) .. nmax.coerceIn(0, size - 1)) {
            val x1 = this.min.getAt(n)
            val x2 = this.max.getAt(n)
            if (intersects(x1, x2, min, max)) {
                out(kotlin.math.max(x1, min), kotlin.math.min(x2, max))
                return true
            }
        }

        return false
    }

    // Use for testing
    // O(n * log(n))
    internal fun intersectionSlow(min: Int, max: Int): Pair<Int, Int>? {
        var out: Pair<Int, Int>? = null
        intersectionSlow(min, max) { x1, x2 -> out = x1 to x2 }
        return out
    }

    // Use for testing
    // O(n^2)
    internal inline fun intersectionSlow(min: Int, max: Int, out: (min: Int, max: Int) -> Unit): Boolean {
        fastForEach { x1, x2 ->
            if (intersects(x1, x2, min, max)) {
                out(kotlin.math.max(x1, min), kotlin.math.min(x2, max))
                return true
            }
        }
        return false
    }

    operator fun contains(v: Int): Boolean {
        val result = findNearIndex(v)
        return result.found
    }

    inline fun fastForEach(block: (min: Int, max: Int) -> Unit) {
        for (n in 0 until size) {
            block(min.getAt(n), max.getAt(n))
        }
    }

    fun setToIntersect(a: IntSegmentSet, b: IntSegmentSet) = this.apply {
        clear().also { a.fastForEach { x1, x2 -> b.intersection(x1, x2) { min, max -> add(min, max) } } }
    }

    // Use for testing
    internal fun setToIntersectSlow(a: IntSegmentSet, b: IntSegmentSet) = this.apply {
        clear().also { a.fastForEach { x1, x2 -> b.intersectionSlow(x1, x2) { min, max -> add(min, max) } } }
    }

    @PublishedApi
    internal fun intersects(x1: Int, x2: Int, y1: Int, y2: Int): Boolean = x2 >= y1 && y2 >= x1
    @PublishedApi
    internal fun intersects(x1: Int, x2: Int, index: Int): Boolean = intersects(x1, x2, min.getAt(index), max.getAt(index))

    @PublishedApi
    internal fun contains(v: Int, x1: Int, x2: Int): Boolean = v in x1 until x2
    @PublishedApi
    internal fun contains(v: Int, index: Int): Boolean = contains(v, min.getAt(index), max.getAt(index))

    override fun toString(): String = buildString {
        append("[")
        var n = 0
        fastForEach { min, max ->
            val first = (n == 0)
            if (!first) append(", ")
            append("$min-$max")
            n++
        }
        append("]")
    }
}

