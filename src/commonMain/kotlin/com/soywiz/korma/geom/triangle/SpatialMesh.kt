package com.soywiz.korma.geom.triangle

import com.soywiz.korma.geom.*
import kotlin.math.*

class SpatialMesh {
    private var mapTriangleToSpatialNode = hashMapOf<Triangle, Node>()
    var nodes = arrayListOf<Node>()

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(triangles: Iterable<Triangle>) {
        for (triangle in triangles) {
            val node = getNodeFromTriangle(triangle)
            if (node != null) nodes.add(node)
        }
    }

    fun spatialNodeFromPoint(point: Point2d): Node {
        for (node in nodes) {
            if (node.triangle!!.pointInsideTriangle(point)) return node
        }
        throw Error("Point2d not inside triangles")
    }

    fun getNodeAt(point: Point2d): Node? {
        for (node in nodes) if (node.triangle!!.containsPoint(point)) return node
        return null
    }

    fun getNodeFromTriangle(triangle: Triangle?): Node? {
        if (triangle === null) return null

        if (!mapTriangleToSpatialNode.containsKey(triangle)) {
            val tp0 = triangle.p0
            val tp1 = triangle.p1
            val tp2 = triangle.p2
            val sn = Node(
                x = ((tp0.x + tp1.x + tp2.x) / 3).toInt().toDouble(),
                y = ((tp0.y + tp1.y + tp2.y) / 3).toInt().toDouble(),
                z = 0.0,
                triangle = triangle,
                G = 0,
                H = 0
            )
            mapTriangleToSpatialNode[triangle] = sn
            sn.neighbors = arrayOf(
                if (triangle.constrained_edge[0]) null else getNodeFromTriangle(triangle.neighbors[0]),
                if (triangle.constrained_edge[1]) null else getNodeFromTriangle(triangle.neighbors[1]),
                if (triangle.constrained_edge[2]) null else getNodeFromTriangle(triangle.neighbors[2])
            )
        }
        return mapTriangleToSpatialNode[triangle]
    }

    class Node(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var z: Double = 0.0,
        var triangle: Triangle? = null,
        var G: Int = 0, // Cost
        var H: Int = 0, // Heuristic
        var neighbors: Array<Node?> = arrayOfNulls(3),
        var parent: Node? = null,
        var closed: Boolean = false
    ) {
        val F: Int get() = G + H // F = G + H

        fun distanceToSpatialNode(that: Node): Int = hypot(this.x - that.x, this.y - that.y).toInt()

        override fun toString(): String = "SpatialNode($x, $y)"
    }

    companion object {
        fun fromTriangles(triangles: Iterable<Triangle>): SpatialMesh = SpatialMesh(triangles)
    }

    override fun toString() = "SpatialMesh(" + nodes.toString() + ")"
}

typealias SpatialNode = SpatialMesh.Node
