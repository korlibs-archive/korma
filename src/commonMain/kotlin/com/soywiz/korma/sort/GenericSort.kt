package com.soywiz.korma.sort

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

// Simplest sort. @TODO: implement TimSort
fun <T> genericSort(subject: T, left: Int, right: Int, compare: (T, Int, Int) -> Int, swap: (T, Int, Int) -> Unit): T {
    for (n in left + 1 .. right) {
        for (m in n downTo left + 1) {
            if (compare(subject, m, m - 1) < 0) {
                swap(subject, m, m -1)
            } else {
                break
            }
        }
    }
    return subject
}
