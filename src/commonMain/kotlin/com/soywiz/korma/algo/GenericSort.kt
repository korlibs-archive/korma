package com.soywiz.korma.algo

import kotlin.math.*

//fun <T> quickSort(subject: T, first: Int, last: Int, compare: (T, Int, Int) -> Int, swap: (T, Int, Int) -> Unit): T {
//    if (last > first) {
//        val pivot = (first + last) / 2
//        var left = first
//        var right = last
//        while (left <= right) {
//            while (compare(subject, left, pivot) < 0) left++
//            while (compare(subject, right, pivot) > 0) right--
//            if (left <= right) {
//                swap(subject, left, right)
//                left++
//                right--
//            }
//        }
//        quickSort(subject, first, right, compare, swap)
//        quickSort(subject, left, last, compare, swap)
//    }
//    return subject
//}

private const val RUN = 32

private fun <T> insertionSort(arr: T, left: Int, right: Int, ops: SortOps<T>) {
    for (n in left + 1..right) {
        var m = n - 1

        while (m >= left) {
            if (ops.compare(arr, m, n) <= 0) break
            m--
        }
        m++

        if (m != n) ops.shiftLeft(arr, m, n)
    }
}

private fun <T> merge(arr: T, start: Int, mid: Int, end: Int, ops: SortOps<T>) {
    var s = start
    var m = mid
    var s2 = m + 1

    if (ops.compare(arr, m, s2) <= 0) return

    while (s <= m && s2 <= end) {
        if (ops.compare(arr, s, s2) <= 0) {
            s++
        } else {
            ops.shiftLeft(arr, s, s2)
            s++
            m++
            s2++
        }
    }
}

private fun <T> mergeSort(arr: T, l: Int, r: Int, ops: SortOps<T>) {
    if (l < r) {
        val m = l + (r - l) / 2
        mergeSort(arr, l, m, ops)
        mergeSort(arr, m + 1, r, ops)
        merge(arr, l, m, r, ops)
    }
}

private fun <T> timSort(arr: T, l: Int, r: Int, ops: SortOps<T>) {
    val n = r - l + 1
    for (i in 0 until n step RUN) {
        insertionSort(arr, l + i, l + min((i + 31), (n - 1)), ops)
    }
    var size = RUN
    while (size < n) {
        for (left in 0 until n step (2 * size)) {
            val mid = left + size - 1
            val right = min((left + 2 * size - 1), (n - 1))
            merge(arr, l + left, l + mid, l + right, ops)
        }
        size *= 2
    }
}

// Simplest sort. @TODO: implement TimSort
fun <T> genericSort(subject: T, left: Int, right: Int, ops: SortOps<T>): T {
    //insertionSort(subject, left, right, ops)
    //mergeSort(subject, left, right, ops)
    timSort(subject, left, right, ops)
    return subject
}

abstract class SortOps<T> {
    abstract fun compare(subject: T, l: Int, r: Int): Int
    abstract fun swap(subject: T, indexL: Int, indexR: Int)
    open fun shiftLeft(subject: T, indexL: Int, indexR: Int) {
        for (n in indexR downTo indexL + 1) swap(subject, n - 1, n)
    }
}

object SortOpsComparable : SortOps<MutableList<Comparable<Any>>>() {
    override fun compare(subject: MutableList<Comparable<Any>>, l: Int, r: Int): Int {
        return subject[l].compareTo(subject[r])
    }

    override fun swap(subject: MutableList<Comparable<Any>>, indexL: Int, indexR: Int) {
        val tmp = subject[indexL]
        subject[indexL] = subject[indexR]
        subject[indexR] = tmp
    }
}

fun <T : Comparable<T>> MutableList<T>.genericSort(left: Int = 0, right: Int = size - 1): MutableList<T> {
    return genericSort(this, left, right, SortOpsComparable as SortOps<MutableList<T>>)
}

fun <T : Comparable<T>> List<T>.genericSorted(left: Int = 0, right: Int = size - 1): List<T> {
    return this.subList(left, right + 1).toMutableList().genericSort()
}
