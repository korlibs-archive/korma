package com.soywiz.korma.buffer

fun IntArray.copyTo(srcPos: Int, dst: IntArray, dstPos: Int, count: Int) {
	for (n in 0 until count) dst[dstPos + n] = this[srcPos + n]
}

fun DoubleArray.copyTo(srcPos: Int, dst: DoubleArray, dstPos: Int, count: Int) {
	this.copyOf()
	for (n in 0 until count) dst[dstPos + n] = this[srcPos + n]
}