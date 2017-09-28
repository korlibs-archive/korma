package com.soywiz.korma.buffer

fun IntArray.copyTo(srcPos: Int, dst: IntArray, dstPos: Int, count: Int) {
	for (n in 0 until count) dst[dstPos + n] = this[srcPos + n]
}

fun DoubleArray.copyTo(srcPos: Int, dst: DoubleArray, dstPos: Int, count: Int) {
	for (n in 0 until count) dst[dstPos + n] = this[srcPos + n]
}

fun IntArray.binarySearch(v: Int, fromIndex: Int, toIndex: Int): Int = TODO()
fun DoubleArray.binarySearch(v: Double, fromIndex: Int, toIndex: Int): Int = TODO()
