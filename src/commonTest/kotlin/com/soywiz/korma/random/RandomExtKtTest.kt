package com.soywiz.korma.random

import kotlin.test.*

class RandomExtKtTest {
    @Test
    fun weighted() {
        val random = MtRandom(0L)
        val weights = Weights("a" to 1.0, "b" to 1.0)
        assertEquals(mapOf("a" to 4997, "b" to 5003), (0 until 10000).map { random.weighted(weights) }.countMap())
    }

    @Test
    fun weighted2() {
        val random = MtRandom(0L)
        val weights = Weights("a" to 1.0, "b" to 4.0)
        assertEquals(mapOf("a" to 2777, "b" to 7223), (0 until 10000).map { random.weighted(weights) }.countMap())
    }

    @Test
    fun weighted3() {
        val random = MtRandom(0L)
        val weights = Weights("a" to 1.0, "b" to 4.0, "c" to 8.0)
        assertEquals(
            mapOf("a" to 972, "b" to 2844, "c" to 6184),
            (0 until 10000).map { random.weighted(weights) }.countMap()
        )
    }

    private fun <T> List<T>.countMap(): Map<T, Int> {
        val counts = hashMapOf<T, Int>()
        for (key in this) {
            if (key !in counts) counts[key] = 0
            counts[key] = counts[key]!! + 1
        }
        return counts
    }
}
