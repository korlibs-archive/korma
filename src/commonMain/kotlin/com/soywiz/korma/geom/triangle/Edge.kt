package com.soywiz.korma.geom.triangle

import com.soywiz.korma.geom.*

data class Edge internal constructor(
    val dummy: Boolean,
    val p: IPoint,
    val q: IPoint
) {
    @Suppress("unused")
    fun hasPoint(point: IPoint): Boolean = (p == point) || (q == point)

    companion object {
        fun getUniquePointsFromEdges(edges: ArrayList<Edge>): List<IPoint> =
            edges.flatMap { listOf(it.p, it.q) }.distinct()

        @Suppress("unused")
        fun traceList(edges: ArrayList<Edge>) {
            val pointsList = getUniquePointsFromEdges(edges)
            val pointsMap = hashMapOf<IPoint, Int>()

            var pointsLength = 0
            for (point in pointsList) pointsMap[point] = ++pointsLength

            fun getPointName(point: IPoint): String = "p" + pointsMap[point]

            println("Points:")
            for (point in pointsList) println("  " + getPointName(point) + " = " + point)
            println("Edges:")
            for (edge in edges) println("  Edge(" + getPointName(edge.p) + ", " + getPointName(edge.q) + ")")
        }
    }


    override fun toString(): String = "Edge(${this.p}, ${this.q})"
}

fun Edge(p1: IPoint, p2: IPoint): Edge {
    val comp = Point.compare(p1, p2)
    if (comp == 0) throw Error("Repeat points")
    val p = if (comp < 0) p1 else p2
    val q = if (comp < 0) p2 else p1
    return Edge(true, p, q)
}
