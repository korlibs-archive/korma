package com.soywiz.korma.geom.binpack

import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.Size

class BinPacker(val width: Double, val height: Double, val algo: BinPack = MaxRects(width, height)) {
	val allocated = arrayListOf<Rectangle>()

	fun add(width: Double, height: Double): Rectangle {
		val rect = algo.add(width, height) ?: throw IllegalStateException("Size '${this.width}x${this.height}' doesn't fit in '${this.width}x${this.height}'")
		allocated += rect
		return rect
	}

	fun <T> addBatch(items: Iterable<T>, getSize: (T) -> Size): List<Pair<T, Rectangle?>> = algo.addBatch(items, getSize)
	fun addBatch(items: Iterable<Size>): List<Rectangle?> = algo.addBatch(items) { it }.map { it.second }
}
