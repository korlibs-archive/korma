/*
Poly2Tri:Fast and Robust Simple Polygon triangulation with/without holes
                        by Sweep Line Algorithm
                               Liang, Wu
        http://www.mema.ucl.ac.be/~wu/Poly2Tri/poly2tri.html
        Copyright (C) 2003, 2004, 2005, ALL RIGHTS RESERVED.

---------------------------------------------------------------------
wu@mema.ucl.ac.be                           wuliang@femagsoft.com
Centre for Sys. Eng. & App. Mech.           FEMAGSoft S.A.
Universite Cathalique de Louvain            4, Avenue Albert Einstein
Batiment Euler, Avenue Georges Lemaitre, 4  B-1348 Louvain-la-Neuve
B-1348, Louvain-la-Neuve                    Belgium
Belgium
---------------------------------------------------------------------

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHAN-
TABILITY or FITNESS FOR A PARTICULAR PURPOSE.

This program may be freely redistributed under the condition that all
the copyright notices in all source files ( including the copyright
notice printed when the `-h' switch is selected) are not removed.Both
the binary and source codes may not be sold or included in any comme-
rcial products without a license from the corresponding author(s) &
entities.

1) Arbitrary precision floating-point arithmetic and fast robust geo-
   metric predicates (predicates.cc) is copyrighted by
   Jonathan Shewchuk (http://www.cs.berkeley.edu/~jrs) and you may get
   the source code from http://www.cs.cmu.edu/~quake/robust.html

2) The shell script mps2eps is copyrighted by Jon Edvardsson
   (http://www.ida.liu.se/~pelab/members/index.php4/?12) and you may
   get the copy from http://www.ida.liu.se/~joned/download/mps2eps/

3) All other source codes and exmaples files distributed in Poly2Tri
   are copyrighted by Liang, Wu (http://www.mema.ucl.ac.be/~wu) and
   FEMAGSoft S.A.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.soywiz.korma.geom.triangle

import com.soywiz.korma.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.geom.internal.*
import kotlin.collections.set
import kotlin.math.*

class AdvancingFront(
    var head: Node,
    @Suppress("unused") var tail: Node
) {
    var searchNode: Node = head

    /*fun findSearchNode(x) {
        return this.search_node;
    }*/

    fun locateNode(x: Double): Node? {
        var node: Node = this.searchNode

        if (x < node.value) {
            while (node.prev != null) {
                node = node.prev!!
                if (x >= node.value) {
                    this.searchNode = node
                    return node
                }
            }
        } else {
            while (node.next != null) {
                node = node.next!!
                if (x < node.value) {
                    this.searchNode = node.prev!!
                    return node.prev!!
                }
            }
        }
        return null
    }

    fun locatePoint(point: Point2d): Node? {
        val px: Double = point.x
        //var node:* = this.FindSearchNode(px);
        var node: Node? = this.searchNode
        val nx: Double = node!!.point.x

        when {
            px == nx -> {
                if (point != (node.point)) {
                    // We might have two nodes with same x value for a short time
                    node = when (point) {
                        (node.prev!!.point) -> node.prev
                        (node.next!!.point) -> node.next
                        else -> throw(Error("Invalid AdvancingFront.locatePoint call!"))
                    }
                }
            }
            px < nx -> {
                node = node.prev
                while (node != null) {
                    if (point == (node.point)) break
                    node = node.prev
                }
            }
            else -> {
                node = node.next
                while (node != null) {
                    if (point == (node.point)) break
                    node = node.next
                }
            }
        }

        if (node != null) this.searchNode = node
        return node
    }

}

class Basin {
    var leftNode: Node? = null
    var bottomNode: Node? = null
    var rightNode: Node? = null
    var width: Double = 0.0
    var leftHighest: Boolean = false

    @Suppress("unused")
    fun clear() {
        this.leftNode = null
        this.bottomNode = null
        this.rightNode = null
        this.width = 0.0
        this.leftHighest = false
    }
}

class Edge internal constructor(
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

class EdgeEvent {
    var constrainedEdge: Edge? = null
    var right: Boolean = false
}

class Node(
    var point: Point2d,
    var triangle: ISpatialTriangle? = null
) {
    var prev: Node? = null
    var next: Node? = null
    var value: Double = this.point.x

    /**
     *
     * @return the angle between 3 front nodes
     */
    val holeAngle: Double
        get() {
            /* Complex plane
             * ab = cosA +i*sinA
             * ab = (ax + ay*i)(bx + by*i) = (ax*bx + ay*by) + i(ax*by-ay*bx)
             * atan2(y,x) computes the principal value of the argument function
             * applied to the complex number x+iy
             * Where x = ax*bx + ay*by
             *       y = ax*by - ay*bx
             */
            val prev = this.prev ?: throw IllegalStateException("Not enough vertices")
            val next = this.next ?: throw IllegalStateException("Not enough vertices")
            val ax: Double = next.point.x - this.point.x
            val ay: Double = next.point.y - this.point.y
            val bx: Double = prev.point.x - this.point.x
            val by: Double = prev.point.y - this.point.y
            return atan2(
                ax * by - ay * bx,
                ax * bx + ay * by
            )
        }

    val basinAngle: Double
        get() {
            val nextNext = this.next?.next ?: throw IllegalStateException("Not enough vertices")
            return atan2(
                this.point.y - nextNext.point.y, // ay
                this.point.x - nextNext.point.x  // ax
            )
        }
}

class EdgeContext {
    val pointsToEdgeLists = hashMapOf<Point2d, ArrayList<Edge>>()
    fun getPointEdgeList(point: Point2d) = pointsToEdgeLists.getOrPut(point) { arrayListOf() }
    fun createEdge(p1: Point2d, p2: Point2d): Edge {
        val comp = Point2d.compare(p1, p2)
        return when (comp) {
            +1 -> Edge(p2, p1)
            -1 -> Edge(p1, p2)
            else -> throw Error("Repeat points")
        }.also {
            getPointEdgeList(it.q).add(it)
        }
    }
}

class Sweep(
    private var context: SweepContext
) {
    val edgeContext get() = context.edgeContext
    /**
     * Triangulate simple polygon with holes.
     */
    fun triangulate() {
        context.initTriangulation()
        context.createAdvancingFront()
        sweepPoints()                    // Sweep points; build mesh
        finalizationPolygon()            // Clean up
    }

    fun sweepPoints() {
        for (i in 1 until this.context.points.size) {
            val point: Point2d = this.context.points.getPoint(i)
            val node: Node = this.pointEvent(point)
            val edgeList = edgeContext.getPointEdgeList(point)
            for (j in 0 until edgeList.size) {
                this.edgeEventByEdge(edgeList[j], node)
            }
        }
    }

    fun finalizationPolygon() {
        // Get an Internal triangle to start with
        val next = this.context.front.head.next!!
        var t: ISpatialTriangle = next.triangle!!
        val p: Point2d = next.point
        while (!t.getConstrainedEdgeCW(p)) t = t.neighborCCW(p)!!

        // Collect interior triangles constrained by edges
        this.context.meshClean(t)
    }

    /**
     * Find closes node to the left of the point and
     * create a triangle. If needed holes and basins
     * will be filled to.
     */
    fun pointEvent(point: Point2d): Node {
        val node = this.context.locateNode(point)!!
        val newNode = newFrontTriangle(point, node)

        // Only need to check +epsilon since point never have smaller
        // x value than node due to how we fetch nodes from the front
        if (point.x <= (node.point.x + Constants.EPSILON)) fill(node)

        //tcx.AddNode(new_node);

        fillAdvancingFront(newNode)
        return newNode
    }

    fun edgeEventByEdge(edge: Edge, node: Node) {
        val edgeEvent = this.context.edgeEvent
        edgeEvent.constrainedEdge = edge
        edgeEvent.right = (edge.p.x > edge.q.x)

        val triangle = node.triangle!!

        if (triangle.isEdgeSide(edge.p, edge.q)) return

        // For now we will do all needed filling
        // TODO: integrate with flip process might give some better performance
        //       but for now this avoid the issue with cases that needs both flips and fills
        this.fillEdgeEvent(edge, node)

        this.edgeEventByPoints(edge.p, edge.q, triangle, edge.q)
    }

    fun edgeEventByPoints(ep: Point2d, eq: Point2d, triangle: ISpatialTriangle, point: Point2d) {
        if (triangle.isEdgeSide(ep, eq)) return

        val p1: Point2d = triangle.pointCCW(point)
        val o1: Orientation = Orientation.orient2d(eq, p1, ep)
        if (o1 == Orientation.COLLINEAR) throw(Error("Sweep.edgeEvent: Collinear not supported!"))

        val p2: Point2d = triangle.pointCW(point)
        val o2: Orientation = Orientation.orient2d(eq, p2, ep)
        if (o2 == Orientation.COLLINEAR) throw(Error("Sweep.edgeEvent: Collinear not supported!"))

        if (o1 == o2) {
            // Need to decide if we are rotating CW or CCW to get to a triangle
            // that will cross edge
            edgeEventByPoints(
                ep,
                eq,
                if (o1 == Orientation.CW) triangle.neighborCCW(point)!! else triangle.neighborCW(point)!!,
                point
            )
        } else {
            // This triangle crosses constraint so lets flippin start!
            flipEdgeEvent(ep, eq, triangle, point)
        }
    }

    fun newFrontTriangle(point: Point2d, node: Node): Node {
        val triangle = Triangle(point, node.point, node.next!!.point)

        triangle.markNeighborTriangle(node.triangle!!)
        this.context.addToSet(triangle)

        val newNode = Node(point)
        newNode.next = node.next
        newNode.prev = node
        node.next!!.prev = newNode
        node.next = newNode

        if (!legalize(triangle)) this.context.mapTriangleToNodes(triangle)

        return newNode
    }

    /**
     * Adds a triangle to the advancing front to fill a hole.
     * @param node - middle node, that is the bottom of the hole
     */
    fun fill(node: Node) {
        val triangle = Triangle(node.prev!!.point, node.point, node.next!!.point)

        // TODO: should copy the constrained_edge value from neighbor triangles
        //       for now constrained_edge values are copied during the legalize
        triangle.markNeighborTriangle(node.prev!!.triangle!!)
        triangle.markNeighborTriangle(node.triangle!!)

        this.context.addToSet(triangle)

        // Update the advancing front
        node.prev!!.next = node.next
        node.next!!.prev = node.prev

        // If it was legalized the triangle has already been mapped
        if (!legalize(triangle)) {
            this.context.mapTriangleToNodes(triangle)
        }

        this.context.removeNode(node)
    }

    /**
     * Fills holes in the Advancing Front
     */
    fun fillAdvancingFront(n: Node) {
        var node: Node
        var angle: Double

        // Fill right holes
        node = n.next!!
        while (node.next != null) {
            angle = node.holeAngle
            if ((angle > Constants.PI_2) || (angle < -Constants.PI_2)) break
            this.fill(node)
            node = node.next!!
        }

        // Fill left holes
        node = n.prev!!
        while (node.prev != null) {
            angle = node.holeAngle
            if ((angle > Constants.PI_2) || (angle < -Constants.PI_2)) break
            this.fill(node)
            node = node.prev!!
        }

        // Fill right basins
        if ((n.next != null) && (n.next!!.next != null)) {
            angle = n.basinAngle
            if (angle < Constants.PI_3div4) this.fillBasin(n)
        }
    }

    /**
     * Returns true if triangle was legalized
     */
    fun legalize(t: ISpatialTriangle): Boolean {
        // To legalize a triangle we start by finding if any of the three edges
        // violate the Delaunay condition
        for (i in 0 until 3) {
            if (t.delaunay_edge[i]) continue
            val ot: ISpatialTriangle = t.neighbors[i] ?: continue
            val p: Point2d = t.point(i)
            val op: Point2d = ot.oppositePoint(t, p)
            val oi: Int = ot.index(op)

            // If this is a Constrained Edge or a Delaunay Edge(only during recursive legalization)
            // then we should not try to legalize
            if (ot.constrained_edge[oi] || ot.delaunay_edge[oi]) {
                t.constrained_edge[i] = ot.constrained_edge[oi]
                continue
            }

            if (Triangle.insideIncircle(p, t.pointCCW(p), t.pointCW(p), op)) {
                // Lets mark this shared edge as Delaunay
                t.delaunay_edge[i] = true
                ot.delaunay_edge[oi] = true

                // Lets rotate shared edge one vertex CW to legalize it
                Triangle.rotateTrianglePair(t, p, ot, op)

                // We now got one valid Delaunay Edge shared by two triangles
                // This gives us 4 edges to check for Delaunay

                // Make sure that triangle to node mapping is done only one time for a specific triangle
                if (!this.legalize(t)) this.context.mapTriangleToNodes(t)
                if (!this.legalize(ot)) this.context.mapTriangleToNodes(ot)

                // Reset the Delaunay edges, since they only are valid Delaunay edges
                // until we add a triangle or point.
                // XXX: need to think about this. Can these edges be tried after we
                //      return to previous recursive level?
                t.delaunay_edge[i] = false
                ot.delaunay_edge[oi] = false

                // If triangle have been legalized no need to check the other edges since
                // the recursive legalization will handles those so we can end here.
                return true
            }
        }
        return false
    }

    /**
     * Fills a basin that has formed on the Advancing Front to the right
     * of given node.<br>
     * First we decide a left,bottom and right node that forms the
     * boundaries of the basin. Then we do a reqursive fill.
     *
     * @param node - starting node, this or next node will be left node
     */
    fun fillBasin(node: Node) {
        val context = this.context
        val basin = context.basin
        basin.leftNode = if (Orientation.orient2d(
                node.point,
                node.next!!.point,
                node.next!!.next!!.point
            ) == Orientation.CCW
        ) node.next!!.next else node.next

        // Find the bottom and right node
        basin.bottomNode = basin.leftNode
        while ((basin.bottomNode!!.next != null) && (basin.bottomNode!!.point.y >= basin.bottomNode!!.next!!.point.y)) {
            basin.bottomNode = basin.bottomNode!!.next
        }

        // No valid basin
        if (basin.bottomNode == basin.leftNode) return

        basin.rightNode = basin.bottomNode
        while ((basin.rightNode!!.next != null) && (basin.rightNode!!.point.y < basin.rightNode!!.next!!.point.y)) {
            basin.rightNode = basin.rightNode!!.next
        }

        // No valid basins
        if (basin.rightNode == basin.bottomNode) return

        basin.width = (basin.rightNode!!.point.x - basin.leftNode!!.point.x)
        basin.leftHighest = (basin.leftNode!!.point.y > basin.rightNode!!.point.y)

        this.fillBasinReq(basin.bottomNode!!)
    }

    /**
     * Recursive algorithm to fill a Basin with triangles
     *
     * @param node - bottom_node
     */
    fun fillBasinReq(node: Node) {
        @Suppress("NAME_SHADOWING")
        var node = node
        // if shallow stop filling
        if (this.isShallow(node)) return

        this.fill(node)

        when {
            node.prev == this.context.basin.leftNode && node.next == this.context.basin.rightNode -> {
                return
            }
            node.prev == this.context.basin.leftNode -> {
                if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CW) {
                    return
                }
                node = node.next!!
            }
            node.next == this.context.basin.rightNode -> {
                if (Orientation.orient2d(node.point, node.prev!!.point, node.prev!!.prev!!.point) == Orientation.CCW) {
                    return
                }
                node = node.prev!!
            }
            else -> {
                // Continue with the neighbor node with lowest Y value
                node = if (node.prev!!.point.y < node.next!!.point.y) node.prev!! else node.next!!
            }
        }

        this.fillBasinReq(node)
    }

    fun isShallow(node: Node): Boolean {
        val height: Double = if (this.context.basin.leftHighest) {
            this.context.basin.leftNode!!.point.y - node.point.y
        } else {
            this.context.basin.rightNode!!.point.y - node.point.y
        }

        // if shallow stop filling
        return (this.context.basin.width > height)
    }

    fun fillEdgeEvent(edge: Edge, node: Node) {
        if (this.context.edgeEvent.right) {
            this.fillRightAboveEdgeEvent(edge, node)
        } else {
            this.fillLeftAboveEdgeEvent(edge, node)
        }
    }

    fun fillRightAboveEdgeEvent(edge: Edge, node: Node) {
        var n = node
        while (n.next!!.point.x < edge.p.x) {
            // Check if next node is below the edge
            if (Orientation.orient2d(edge.q, n.next!!.point, edge.p) == Orientation.CCW) {
                this.fillRightBelowEdgeEvent(edge, n)
            } else {
                n = n.next!!
            }
        }
    }

    fun fillRightBelowEdgeEvent(edge: Edge, node: Node) {
        if (node.point.x >= edge.p.x) return
        if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CCW) {
            // Concave
            this.fillRightConcaveEdgeEvent(edge, node)
        } else {
            this.fillRightConvexEdgeEvent(edge, node) // Convex
            this.fillRightBelowEdgeEvent(edge, node) // Retry this one
        }
    }

    fun fillRightConcaveEdgeEvent(edge: Edge, node: Node) {
        this.fill(node.next!!)
        if (node.next!!.point != edge.p) {
            // Next above or below edge?
            if (Orientation.orient2d(edge.q, node.next!!.point, edge.p) == Orientation.CCW) {
                // Below
                if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CCW) {
                    // Next is concave
                    this.fillRightConcaveEdgeEvent(edge, node)
                } else {
                    // Next is convex
                }
            }
        }
    }

    fun fillRightConvexEdgeEvent(edge: Edge, node: Node) {
        // Next concave or convex?
        if (Orientation.orient2d(
                node.next!!.point,
                node.next!!.next!!.point,
                node.next!!.next!!.next!!.point
            ) == Orientation.CCW
        ) {
            // Concave
            this.fillRightConcaveEdgeEvent(edge, node.next!!)
        } else {
            // Convex
            // Next above or below edge?
            if (Orientation.orient2d(edge.q, node.next!!.next!!.point, edge.p) == Orientation.CCW) {
                // Below
                this.fillRightConvexEdgeEvent(edge, node.next!!)
            } else {
                // Above
            }
        }
    }

    fun fillLeftAboveEdgeEvent(edge: Edge, node: Node) {
        var n = node
        while (n.prev!!.point.x > edge.p.x) {
            // Check if next node is below the edge
            if (Orientation.orient2d(edge.q, n.prev!!.point, edge.p) == Orientation.CW) {
                this.fillLeftBelowEdgeEvent(edge, n)
            } else {
                n = n.prev!!
            }
        }
    }

    fun fillLeftBelowEdgeEvent(edge: Edge, node: Node) {
        if (node.point.x > edge.p.x) {
            if (Orientation.orient2d(node.point, node.prev!!.point, node.prev!!.prev!!.point) == Orientation.CW) {
                // Concave
                this.fillLeftConcaveEdgeEvent(edge, node)
            } else {
                // Convex
                this.fillLeftConvexEdgeEvent(edge, node)
                // Retry this one
                this.fillLeftBelowEdgeEvent(edge, node)
            }
        }
    }

    fun fillLeftConvexEdgeEvent(edge: Edge, node: Node) {
        // Next concave or convex?
        if (Orientation.orient2d(
                node.prev!!.point,
                node.prev!!.prev!!.point,
                node.prev!!.prev!!.prev!!.point
            ) == Orientation.CW
        ) {
            // Concave
            this.fillLeftConcaveEdgeEvent(edge, node.prev!!)
        } else {
            // Convex
            // Next above or below edge?
            if (Orientation.orient2d(edge.q, node.prev!!.prev!!.point, edge.p) == Orientation.CW) {
                // Below
                this.fillLeftConvexEdgeEvent(edge, node.prev!!)
            } else {
                // Above
            }
        }
    }

    fun fillLeftConcaveEdgeEvent(edge: Edge, node: Node) {
        this.fill(node.prev!!)
        if (node.prev!!.point != edge.p) {
            // Next above or below edge?
            if (Orientation.orient2d(edge.q, node.prev!!.point, edge.p) == Orientation.CW) {
                // Below
                if (Orientation.orient2d(node.point, node.prev!!.point, node.prev!!.prev!!.point) == Orientation.CW) {
                    // Next is concave
                    this.fillLeftConcaveEdgeEvent(edge, node)
                } else {
                    // Next is convex
                }
            }
        }
    }

    fun flipEdgeEvent(ep: Point2d, eq: Point2d, t: ISpatialTriangle, p: Point2d) {
        var tt = t
        val ot: ISpatialTriangle = tt.neighborAcross(p) ?: throw Error("[BUG:FIXME] FLIP failed due to missing triangle!")
        // If we want to integrate the fillEdgeEvent do it here
        // With current implementation we should never get here

        val op: Point2d = ot.oppositePoint(tt, p)

        if (Triangle.inScanArea(p, tt.pointCCW(p), tt.pointCW(p), op)) {
            // Lets rotate shared edge one vertex CW
            Triangle.rotateTrianglePair(tt, p, ot, op)
            this.context.mapTriangleToNodes(tt)
            this.context.mapTriangleToNodes(ot)

            // @TODO: equals?
            if ((p == eq) && (op == ep)) {
                if ((eq == this.context.edgeEvent.constrainedEdge!!.q) && (ep == this.context.edgeEvent.constrainedEdge!!.p)) {
                    tt.markConstrainedEdgeByPoints(ep, eq)
                    ot.markConstrainedEdgeByPoints(ep, eq)
                    this.legalize(tt)
                    this.legalize(ot)
                } else {
                    // XXX: I think one of the triangles should be legalized here?
                }
            } else {
                val o: Orientation = Orientation.orient2d(eq, op, ep)
                tt = this.nextFlipTriangle(o, tt, ot, p, op)
                this.flipEdgeEvent(ep, eq, tt, p)
            }
        } else {
            val newP: Point2d = nextFlipPoint(ep, eq, ot, op)
            this.flipScanEdgeEvent(ep, eq, tt, ot, newP)
            this.edgeEventByPoints(ep, eq, tt, p)
        }
    }

    fun nextFlipTriangle(o: Orientation, t: ISpatialTriangle, ot: ISpatialTriangle, p: Point2d, op: Point2d): ISpatialTriangle {
        val tt = if (o == Orientation.CCW) ot else t
        // ot is not crossing edge after flip
        tt.delaunay_edge[tt.edgeIndex(p, op)] = true
        this.legalize(tt)
        tt.clearDelunayEdges()
        return if (o == Orientation.CCW) t else ot
    }

    companion object {
        fun nextFlipPoint(ep: Point2d, eq: Point2d, ot: ITriangle, op: Point2d): Point2d {
            return when (Orientation.orient2d(eq, op, ep)) {
                Orientation.CW -> ot.pointCCW(op) // Right
                Orientation.CCW -> ot.pointCW(op) // Left
                else -> throw Error("[Unsupported] Sweep.NextFlipPoint: opposing point on constrained edge!")
            }
        }
    }

    fun flipScanEdgeEvent(ep: Point2d, eq: Point2d, flip_triangle: ITriangle, t: ISpatialTriangle, p: Point2d) {
        val ot = t.neighborAcross(p)
            ?: throw Error("[BUG:FIXME] FLIP failed due to missing triangle") // If we want to integrate the fillEdgeEvent do it here With current implementation we should never get here

        val op = ot.oppositePoint(t, p)

        if (Triangle.inScanArea(eq, flip_triangle.pointCCW(eq), flip_triangle.pointCW(eq), op)) {
            // flip with edge op.eq
            this.flipEdgeEvent(eq, op, ot, op)
            // TODO: Actually I just figured out that it should be possible to
            //       improve this by getting the next ot and op before the the above
            //       flip and continue the flipScanEdgeEvent here
            // set ot and op here and loop back to inScanArea test
            // also need to set a flip_triangle first
            // Turns out at first glance that this is somewhat complicated
            // so it will have to wait.
        } else {
            val newP: Point2d = nextFlipPoint(ep, eq, ot, op)
            this.flipScanEdgeEvent(ep, eq, flip_triangle, ot, newP)
        }
    }
}

class SweepContext() {
    var triangles: ArrayList<ISpatialTriangle> = ArrayList()
    var points: PointArrayList = PointArrayList()
    var edgeList: ArrayList<Edge> = ArrayList()
    val edgeContext = EdgeContext()

    val set = LinkedHashSet<ISpatialTriangle>()

    lateinit var front: AdvancingFront
    lateinit var head: Point2d
    lateinit var tail: Point2d

    val basin: Basin = Basin()
    var edgeEvent = EdgeEvent()

    constructor(polyline: List<Point2d>) : this() {
        this.addPolyline(polyline)
    }

    private fun addPoints(points: List<Point2d>) {
        for (point in points) this.points.add(point)
    }

    fun addPolyline(polyline: List<Point2d>) {
        this.initEdges(polyline)
        this.addPoints(polyline)
    }

    /**
     * An alias of addPolyline.
     *
     * @param    polyline
     */
    fun addHole(polyline: List<Point2d>) {
        addPolyline(polyline)
    }

    private fun initEdges(polyline: List<Point2d>) {
        for (n in 0 until polyline.size) {
            this.edgeList.add(edgeContext.createEdge(polyline[n], polyline[(n + 1) % polyline.size]))
        }
    }

    fun addToSet(triangle: ISpatialTriangle) {
        this.set += triangle
    }

    companion object {
        /*
		 * Inital triangle factor, seed triangle will extend 30% of
		 * PointSet width to both left and right.
		 */
        private const val kAlpha: Double = 0.3
    }

    fun initTriangulation() {
        var xmin: Double = this.points.getX(0)
        var xmax: Double = this.points.getX(0)
        var ymin: Double = this.points.getY(0)
        var ymax: Double = this.points.getY(0)

        // Calculate bounds
        for (n in 0 until this.points.size) {
            val px = this.points.getX(n)
            val py = this.points.getY(n)
            if (px > xmax) xmax = px
            if (px < xmin) xmin = px
            if (py > ymax) ymax = py
            if (py < ymin) ymin = py
        }

        val dx: Double = kAlpha * (xmax - xmin)
        val dy: Double = kAlpha * (ymax - ymin)
        this.head = Point2d(xmax + dx, ymin - dy)
        this.tail = Point2d(xmin - dy, ymin - dy)

        // Sort points along y-axis
        Vector2.sortPoints(this.points)
        //throw(Error("@TODO Implement 'Sort points along y-axis' @see class SweepContext"));
    }

    fun locateNode(point: Point2d): Node? = this.front.locateNode(point.x)

    fun createAdvancingFront() {
        // Initial triangle
        val triangle = Triangle(this.points.getPoint(0), this.tail, this.head)

        addToSet(triangle)

        val head = Node(triangle.p1, triangle)
        val middle = Node(triangle.p0, triangle)
        val tail = Node(triangle.p2)

        this.front = AdvancingFront(head, tail)

        head.next = middle
        middle.next = tail
        middle.prev = head
        tail.prev = middle
    }

    fun removeNode(@Suppress("UNUSED_PARAMETER") node: Node) {
        // do nothing
    }

    fun mapTriangleToNodes(triangle: ISpatialTriangle) {
        for (n in 0 until 3) {
            if (triangle.neighbors[n] == null) {
                val neighbor: Node? = this.front.locatePoint(triangle.pointCW(triangle.point(n)))
                if (neighbor != null) neighbor.triangle = triangle
            }
        }
    }

    @Suppress("unused")
    fun removeFromMap(triangle: ISpatialTriangle) {
        this.set -= triangle
    }

    fun meshClean(triangle: ISpatialTriangle?, level: Int = 0) {
        if (level == 0) {
            //for each (var mappedTriangle:Triangle in this.map) println(mappedTriangle);
        }
        if (triangle == null || triangle.interior) return
        triangle.interior = true
        this.triangles.add(triangle)
        for (n in 0 until 3) {
            if (!triangle.constrained_edge[n]) {
                this.meshClean(triangle.neighbors[n], level + 1)
            }
        }
    }
}
