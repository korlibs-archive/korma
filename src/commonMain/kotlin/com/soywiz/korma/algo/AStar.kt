package com.soywiz.korma.algo

import com.soywiz.kds.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.math.*

private inline class FinderNode(val index: Int)

object AStar {
    fun find(
        board: Array2<Boolean>, x0: Int, y0: Int, x1: Int, y1: Int, findClosest: Boolean = false,
        diagonals: Boolean = false
    ): List<PointInt> = Finder(board.width, board.height) { x, y -> board[x, y] }.find(x0, y0, x1, y1, findClosest, diagonals)

    class Finder(val width: Int, val height: Int, val isBlocking: (x: Int, y: Int) -> Boolean) {
        private val NULL = FinderNode(-1)

        private val posX = IntArray(width * height) { it % width }
        private val posY = IntArray(width * height) { it / width }
        private val weights = IntArray(width * height) { Int.MAX_VALUE }
        private val prev = IntArray(width * height) { NULL.index }
        private val queue = IntPriorityQueue { a, b -> FinderNode(a).weight - FinderNode(b).weight }

        private fun inside(x: Int, y: Int): Boolean = (x in 0 until width) && (y in 0 until height)
        private fun getNode(x: Int, y: Int): FinderNode = FinderNode(y * width + x)

        private val FinderNode.posX: Int get() = this@Finder.posX[index]
        private val FinderNode.posY: Int get() = this@Finder.posY[index]
        private val FinderNode.value: Boolean get() = isBlocking(posX, posY)
        private var FinderNode.weight: Int
            set(value) = run { this@Finder.weights[index] = value }
            get() = this@Finder.weights[index]
        private var FinderNode.prev: FinderNode
            set(value) = run { this@Finder.prev[index] = value.index }
            get() = FinderNode(this@Finder.prev[index])

        private inline fun FinderNode.neighborhoods(diagonals: Boolean, emit: (FinderNode) -> Unit) {
            for (dy in -1 .. +1) {
                for (dx in -1 .. +1) {
                    if (dx == 0 && dy == 0) continue
                    if (!diagonals && dx != 0 && dy != 0) continue
                    val x = posX + dx
                    val y = posY + dy
                    if (inside(x, y) && !getNode(x, y).value) {
                        emit(getNode(x, y))
                    }
                }
            }
        }

        fun find(x0: Int, y0: Int, x1: Int, y1: Int, findClosest: Boolean = false, diagonals: Boolean = false, emit: (Int, Int) -> Unit) {
            // Reset
            queue.clear()
            for (n in weights.indices) weights[n] = Int.MAX_VALUE
            for (n in prev.indices) prev[n] = NULL.index

            val first = getNode(x0, y0)
            val dest = getNode(x1, y1)
            var closest = first
            var closestDist = distance(x0, y0, x1, y1)
            if (!first.value) {
                queue.add(first.index)
                first.weight = 0
            }

            while (queue.isNotEmpty()) {
                val last = FinderNode(queue.removeHead())
                val dist = distance(last.posX, last.posY, dest.posX, dest.posY)
                if (dist < closestDist) {
                    closestDist = dist
                    closest = last
                }
                val nweight = last.weight + 1
                last.neighborhoods(diagonals) { n ->
                    if (nweight < n.weight) {
                        n.prev = last
                        queue.add(n.index)
                        n.weight = nweight
                    }
                }
            }

            if (findClosest || closest == dest) {
                var current: FinderNode = closest
                while (current != NULL) {
                    emit(current.posX, current.posY)
                    current = current.prev
                }
            }
        }

        fun find(x0: Int, y0: Int, x1: Int, y1: Int, findClosest: Boolean = false, diagonals: Boolean = false): List<PointInt> {
            val out = arrayListOf<PointInt>()
            find(x0, y0, x1, y1, findClosest, diagonals) { x, y -> out += PointInt(x, y) }
            out.reverse()
            return out
        }
    }
}
