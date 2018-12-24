package com.soywiz.korma.geom.triangle

import com.soywiz.korma.geom.*

data class Edge internal constructor(
    val dummy: Boolean,
    val p: Point2d,
    val q: Point2d
) {
    @Suppress("unused")
    fun hasPoint(point: Point2d): Boolean = (p == point) || (q == point)

    companion object {
        fun getUniquePointsFromEdges(edges: ArrayList<Edge>): List<Point2d> =
            edges.flatMap { listOf(it.p, it.q) }.distinct()

        @Suppress("unused")
        fun traceList(edges: ArrayList<Edge>) {
            val pointsList = getUniquePointsFromEdges(edges)
            val pointsMap = hashMapOf<Point2d, Int>()

            var pointsLength = 0
            for (point in pointsList) pointsMap[point] = ++pointsLength

            fun getPointName(point: Point2d): String = "p" + pointsMap[point]

            println("Points:")
            for (point in pointsList) println("  " + getPointName(point) + " = " + point)
            println("Edges:")
            for (edge in edges) println("  Edge(" + getPointName(edge.p) + ", " + getPointName(edge.q) + ")")
        }
    }


    override fun toString(): String = "Edge(${this.p}, ${this.q})"
}

fun Edge(p1: Point2d, p2: Point2d): Edge {
    val comp = Point2d.compare(p1, p2)
    if (comp == 0) throw Error("Repeat points")
    val p = if (comp < 0) p1 else p2
    val q = if (comp < 0) p2 else p1
    return Edge(true, p, q)
}
