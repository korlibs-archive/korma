package com.soywiz.korma.geom.binpack

import com.soywiz.korma.geom.*

class BinPacker(val width: Float, val height: Float, val algo: Algo = MaxRects(width, height)) {
    interface Algo {
        fun add(width: Float, height: Float): Rectangle?
    }

    class Result<T>(val maxWidth: Float, val maxHeight: Float, val items: List<Pair<T, Rectangle>>) {
        val width = items.map { it.second.right }.maxBy { it } ?: 0f
        val height = items.map { it.second.bottom }.maxBy { it } ?: 0f
        val rects get() = items.map { it.second }
        val rectsStr: String get() = rects.toString()
    }

    val allocated = arrayListOf<Rectangle>()

    fun <T> Algo.addBatch(items: Iterable<T>, getSize: (T) -> Size): List<Pair<T, Rectangle?>> {
        val its = items.toList()
        val out = hashMapOf<T, Rectangle?>()
        val sorted = its.map { it to getSize(it) }.sortedByDescending { it.second.area }
        for ((i, size) in sorted) out[i] = this.add(size.width, size.height)
        return its.map { it to out[it] }
    }

    fun add(width: Float, height: Float): Rectangle = addOrNull(width, height)
        ?: throw IllegalStateException("Size '${this.width}x${this.height}' doesn't fit in '${this.width}x${this.height}'")

    fun addOrNull(width: Float, height: Float): Rectangle? {
        val rect = algo.add(width, height) ?: return null
        allocated += rect
        return rect
    }

    fun <T> addBatch(items: Iterable<T>, getSize: (T) -> Size): List<Pair<T, Rectangle?>> =
        algo.addBatch(items, getSize)

    fun addBatch(items: Iterable<Size>): List<Rectangle?> = algo.addBatch(items) { it }.map { it.second }

    companion object {
        inline operator fun invoke(width: Number, height: Number, algo: Algo = MaxRects(width.toFloat(), height.toFloat())) = BinPacker(width.toFloat(), height.toFloat(), algo)

        fun <T> pack(width: Float, height: Float, items: Iterable<T>, getSize: (T) -> Size) =
            BinPacker(width, height).addBatch(items, getSize)

        inline fun <T : Sizeable> packSeveral(
            maxWidth: Number,
            maxHeight: Number,
            items: Iterable<T>
        ): List<Result<T>> = packSeveral(maxWidth.toFloat(), maxHeight.toFloat(), items) { it.size }

        fun <T> packSeveral(
            maxWidth: Float,
            maxHeight: Float,
            items: Iterable<T>,
            getSize: (T) -> Size
        ): List<Result<T>> {
            var currentBinPacker = BinPacker(maxWidth, maxHeight)
            var currentPairs = arrayListOf<Pair<T, Rectangle>>()
            val sortedItems = items.sortedByDescending { getSize(it).area }
            if (sortedItems.any { getSize(it).let { size -> size.width > maxWidth || size.height > maxHeight } }) {
                throw IllegalArgumentException("Item is bigger than max size")
            }

            val out = arrayListOf<Result<T>>()

            fun emit() {
                if (currentPairs.isEmpty()) return
                out += Result(maxWidth, maxHeight, currentPairs.toList())
                currentPairs = arrayListOf()
                currentBinPacker = BinPacker(maxWidth, maxHeight)
            }

            //for (item in items) {
            //	var done = false
            //	while (!done) {
            //		try {
            //			val size = getSize(item)
            //			val rect = currentBinPacker.add(size.width, size.height)
            //			currentPairs.add(item to rect)
            //			done = true
            //		} catch (e: IllegalStateException) {
            //			emit()
            //		}
            //	}
            //}

            for (item in items) {
                var done = false
                while (!done) {
                    val size = getSize(item)
                    val rect = currentBinPacker.addOrNull(size.width, size.height)
                    if (rect != null) {
                        currentPairs.add(item to rect)
                        done = true
                    } else {
                        emit()
                    }
                }
            }
            emit()

            return out
        }
    }
}
