package com.soywiz.korma.sort

fun <T> quickSort(subject: T, left: Int, right: Int, compare: (T, Int, Int) -> Int, swap: (T, Int, Int) -> Unit): T {
    var i = left
    var j = right
    val pivot = (left + right) / 2
    while (i <= j) {
        while (compare(subject, i, pivot) < 0) i++
        while (compare(subject, j, pivot) > 0) j--
        if (i <= j) {
            swap(subject, i, j)
            i++
            j--
        }
    }
    if (left < i - 1) quickSort(subject, left, i - 1, compare, swap)
    if (i < right) quickSort(subject, i, right, compare, swap)
    return subject
}
