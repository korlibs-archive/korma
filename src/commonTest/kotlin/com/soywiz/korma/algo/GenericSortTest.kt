package com.soywiz.korma.algo

import com.soywiz.korma.*
import com.soywiz.korma.geom.ds.*
import kotlin.test.*

class GenericSortTest {
    @Test
    fun test1() {
        assertEquals(listOf(0, 1, 2, 3, 4), listOf(1, 2, 3, 4, 0).genericSorted())
        assertEquals(listOf(0, 1, 2, 3, 4), listOf(1, 2, 3, 0, 4).genericSorted())
        assertEquals(listOf(0, 1, 2, 3, 4), listOf(1, 2, 0, 3, 4).genericSorted())
        assertEquals(listOf(0, 1, 2, 3, 4), listOf(1, 0, 2, 3, 4).genericSorted())
        assertEquals(listOf(0, 1, 2, 3, 4), listOf(0, 1, 2, 3, 4).genericSorted())

        assertEquals(listOf(0), listOf(0).genericSorted())
        assertEquals(listOf(0, 1), listOf(1, 0).genericSorted())
    }

    @Test
    fun test() {
        var compareCount = 0
        var swapCount = 0
        val result = genericSort(arrayListOf(10, 30, 20, 10, 5, 3, 40, 7), 0, 7, object : SortOps<ArrayList<Int>>() {
            override fun ArrayList<Int>.compare(l: Int, r: Int): Int = this[l].compareTo(this[r]).also { compareCount++ }
            override fun ArrayList<Int>.swapIndices(l: Int, r: Int) = this.swap(l, r).also { swapCount++ }
        })
        assertEquals(listOf(3, 5, 7, 10, 10, 20, 30, 40), result)
        assertEquals("22,17", "$compareCount,$swapCount")
    }

    @Test
    fun test2() {
        val points = PointArrayList { add(100, 100).add(400, 400).add(200, 100).add(0, 500).add(-100, 300).add(300, 100) }
        assertEquals("[(100, 100), (400, 400), (200, 100), (0, 500), (-100, 300), (300, 100)]", points.toString())
        points.sort()
        assertEquals("[(100, 100), (200, 100), (300, 100), (-100, 300), (400, 400), (0, 500)]", points.toString())
    }
}

fun <T> MutableList<T>.swap(indexA: Int, indexB: Int) {
    val tmp = this[indexA]
    this[indexA] = this[indexB]
    this[indexB] = tmp
}
