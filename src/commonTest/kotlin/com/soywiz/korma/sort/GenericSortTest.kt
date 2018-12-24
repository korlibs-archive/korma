package com.soywiz.korma.sort

import kotlin.test.*

class GenericSortTest {
    @Test
    fun test() {
        val result = quickSort(arrayListOf(10, 30, 20, 10, 5, 3, 40, 7), 0, 7, { s, x, y -> s[x].compareTo(s[y]) }, { p, x, y -> p.swap(x, y) })
        assertEquals(listOf(3, 5, 7, 10, 10, 20, 30, 40), result)
    }
}

fun <T> MutableList<T>.swap(indexA: Int, indexB: Int) {
    val tmp = this[indexA]
    this[indexA] = this[indexB]
    this[indexB] = tmp
}
