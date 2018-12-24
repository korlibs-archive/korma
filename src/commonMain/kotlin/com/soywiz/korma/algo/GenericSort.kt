package com.soywiz.korma.algo

import kotlin.math.*

fun <T> genericSort(subject: T, left: Int, right: Int, ops: SortOps<T>): T = subject.also { ops.timSort(it, left, right) }
//fun <T> genericSort(subject: T, left: Int, right: Int, ops: SortOps<T>): T = subject.also { ops.insertionSort(it, left, right) }
//fun <T> genericSort(subject: T, left: Int, right: Int, ops: SortOps<T>): T = subject.also { ops.mergeSort(it, left, right) }
fun <T : Comparable<T>> MutableList<T>.genericSort(left: Int = 0, right: Int = size - 1): MutableList<T> = genericSort(this, left, right, SortOpsComparable as SortOps<MutableList<T>>)
fun <T : Comparable<T>> List<T>.genericSorted(left: Int = 0, right: Int = size - 1): List<T> = this.subList(left, right + 1).toMutableList().genericSort()

private fun <T> SortOps<T>.insertionSort(arr: T, left: Int, right: Int) {
    for (n in left + 1..right) {
        var m = n - 1

        while (m >= left) {
            if (arr.compare(m, n) <= 0) break
            m--
        }
        m++

        if (m != n) arr.shiftLeft(m, n)
    }
}

private fun <T> SortOps<T>.merge(arr: T, start: Int, mid: Int, end: Int) {
    var s = start
    var m = mid
    var s2 = m + 1

    if (arr.compare(m, s2) <= 0) return

    while (s <= m && s2 <= end) {
        if (arr.compare(s, s2) <= 0) {
            s++
        } else {
            arr.shiftLeft(s, s2)
            s++
            m++
            s2++
        }
    }
}

private fun <T> SortOps<T>.mergeSort(arr: T, l: Int, r: Int) {
    if (l < r) {
        val m = l + (r - l) / 2
        mergeSort(arr, l, m)
        mergeSort(arr, m + 1, r)
        merge(arr, l, m, r)
    }
}

private fun <T> SortOps<T>.timSort(arr: T, l: Int, r: Int) {
    val RUN = 32

    val n = r - l + 1
    for (i in 0 until n step RUN) {
        insertionSort(arr, l + i, l + min((i + 31), (n - 1)))
    }
    var size = RUN
    while (size < n) {
        for (left in 0 until n step (2 * size)) {
            val mid = left + size - 1
            val right = min((left + 2 * size - 1), (n - 1))
            merge(arr, l + left, l + mid, l + right)
        }
        size *= 2
    }
}

abstract class SortOps<T> {
    abstract fun T.compare(l: Int, r: Int): Int
    abstract fun T.swapIndices(l: Int, r: Int)
    open fun T.shiftLeft(l: Int, r: Int) = run { for (n in r downTo l + 1) swapIndices(n - 1, n) }
}

object SortOpsComparable : SortOps<MutableList<Comparable<Any>>>() {
    override fun MutableList<Comparable<Any>>.compare(l: Int, r: Int): Int = this[l].compareTo(this[r])
    override fun MutableList<Comparable<Any>>.swapIndices(l: Int, r: Int) {
        val tmp = this[l]
        this[l] = this[r]
        this[r] = tmp
    }
}
