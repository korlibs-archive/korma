package com.soywiz.korma.sort

import com.soywiz.korma.*
import com.soywiz.korma.geom.ds.*
import kotlin.test.*

class GenericSortTest {
    @Test
    fun test() {
        val result = genericSort(arrayListOf(10, 30, 20, 10, 5, 3, 40, 7), 0, 7, { s, x, y -> s[x].compareTo(s[y]) }, { p, x, y -> p.swap(x, y) })
        assertEquals(listOf(3, 5, 7, 10, 10, 20, 30, 40), result)
    }

    @Test
    fun test2() {
        val points = PointArrayList { add(100, 100).add(400, 400).add(200, 100).add(0, 500).add(-100, 300).add(300, 100) }
        assertEquals("[(100, 100), (400, 400), (200, 100), (0, 500), (-100, 300), (300, 100)]", points.toString())
        Vector2.sortPoints(points)
        assertEquals("[(100, 100), (200, 100), (300, 100), (-100, 300), (400, 400), (0, 500)]", points.toString())
    }
}

fun <T> MutableList<T>.swap(indexA: Int, indexB: Int) {
    val tmp = this[indexA]
    this[indexA] = this[indexB]
    this[indexB] = tmp
}
