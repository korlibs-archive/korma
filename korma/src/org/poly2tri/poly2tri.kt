package org.poly2tri

import com.soywiz.korio.error.invalidOp
import com.soywiz.korio.util.niceStr
import java.util.*
import kotlin.collections.LinkedHashSet

class AdvancingFront(
	var head: Node,
	var tail: Node
) {
	var search_node: Node = head

	/*fun findSearchNode(x) {
		return this.search_node;
	}*/

	fun locateNode(x: Double): Node? {
		var node: Node = this.search_node

		if (x < node.value) {
			while (node.prev != null) {
				node = node.prev!!
				if (x >= node.value) {
					this.search_node = node
					return node
				}
			}
		} else {
			while (node.next != null) {
				node = node.next!!
				if (x < node.value) {
					this.search_node = node.prev!!
					return node.prev!!
				}
			}
		}
		return null
	}

	fun locatePoint(point: Point): Node? {
		val px: Double = point.x
		//var node:* = this.FindSearchNode(px);
		var node: Node? = this.search_node
		val nx: Double = node!!.point.x

		if (px == nx) {
			if (point != (node.point)) {
				// We might have two nodes with same x value for a short time
				if (point == (node.prev!!.point)) {
					node = node.prev
				} else if (point == (node.next!!.point)) {
					node = node.next
				} else {
					throw(Error("Invalid AdvancingFront.locatePoint call!"))
				}
			}
		} else if (px < nx) {
			node = node.prev
			while (node != null) {
				if (point == (node.point)) break
				node = node.prev
			}
		} else {
			node = node.next
			while (node != null) {
				if (point == (node.point)) break
				node = node.next
			}
		}

		if (node != null) this.search_node = node
		return node
	}

}

class Basin {
	var left_node: Node? = null
	var bottom_node: Node? = null
	var right_node: Node? = null
	var width: Double = 0.0
	var left_highest: Boolean = false

	fun clear(): Unit {
		this.left_node = null
		this.bottom_node = null
		this.right_node = null
		this.width = 0.0
		this.left_highest = false
	}
}

object Constants {
	/*
	 * Inital triangle factor, seed triangle will extend 30% of
	 * PointSet width to both left and right.
	 */
	const val kAlpha: Double = 0.3
	const val EPSILON: Double = 1e-12
	const val PI_2: Double = Math.PI / 2
	const val PI_3div4: Double = 3 * Math.PI / 4
}

class Edge(
	var p1: Point,
	var p2: Point
) {
	var p: Point
	var q: Point

	/// Constructor
	init {
		var swap: Boolean = false

		if (p1.y > p2.y) {
			swap = true
		} else if (p1.y == p2.y) {
			if (p1.x == p2.x) throw Error("Repeat points")
			swap = (p1.x > p2.x)
		} else {
			swap = false
		}

		if (swap) {
			this.q = p1
			this.p = p2
		} else {
			this.p = p1
			this.q = p2
		}

		this.q.edge_list.add(this)
	}

	fun hasPoint(point: Point): Boolean = (p == point) || (q == point)

	companion object {
		fun getUniquePointsFromEdges(edges: ArrayList<Edge>): List<Point> = Point.getUniqueList(edges.flatMap { listOf(it.p, it.q) })

		fun traceList(edges: ArrayList<Edge>): Unit {
			val pointsList = Edge.getUniquePointsFromEdges(edges)
			val pointsMap = hashMapOf<Point, Int>()

			var points_length = 0
			for (point in pointsList) pointsMap[point] = ++points_length

			fun getPointName(point: Point): String = "p" + pointsMap[point]

			println("Points:")
			for (point in pointsList) println("  " + getPointName(point) + " = " + point)
			println("Edges:")
			for (edge in edges) println("  Edge(" + getPointName(edge.p) + ", " + getPointName(edge.q) + ")")
		}
	}


	override fun toString(): String = "Edge(${this.p}, ${this.q})"
}

class EdgeEvent {
	var constrained_edge: Edge? = null
	var right: Boolean = false
}

class Node(
	var point: Point,
	var triangle: Triangle? = null
) {
	var prev: Node? = null
	var next: Node? = null
	var value: Double = this.point.x

	/**
	 *
	 * @param node - middle node
	 * @return the angle between 3 front nodes
	 */
	val holeAngle: Double get() {
		/* Complex plane
		 * ab = cosA +i*sinA
		 * ab = (ax + ay*i)(bx + by*i) = (ax*bx + ay*by) + i(ax*by-ay*bx)
		 * atan2(y,x) computes the principal value of the argument function
		 * applied to the complex number x+iy
		 * Where x = ax*bx + ay*by
		 *       y = ax*by - ay*bx
		 */
		val prev = this.prev ?: invalidOp("Not enough vertices")
		val next = this.next ?: invalidOp("Not enough vertices")
		val ax: Double = next.point.x - this.point.x
		val ay: Double = next.point.y - this.point.y
		val bx: Double = prev.point.x - this.point.x
		val by: Double = prev.point.y - this.point.y
		return Math.atan2(
			ax * by - ay * bx,
			ax * bx + ay * by
		)
	}

	val basinAngle: Double get() {
		val nextNext = this.next?.next ?: invalidOp("Not enough vertices")
		return Math.atan2(
			this.point.y - nextNext.point.y, // ay
			this.point.x - nextNext.point.x  // ax
		)
	}
}

enum class Orientation(val value: Int) {
	CW(+1), CCW(-1), COLLINEAR(0);

	companion object {
		fun orient2d(pa: Point, pb: Point, pc: Point): Orientation {
			val detleft: Double = (pa.x - pc.x) * (pb.y - pc.y)
			val detright: Double = (pa.y - pc.y) * (pb.x - pc.x)
			val `val`: Double = detleft - detright

			if ((`val` > -Constants.EPSILON) && (`val` < Constants.EPSILON)) return Orientation.COLLINEAR
			if (`val` > 0) return Orientation.CCW
			return Orientation.CW
		}
	}
}

/**
 *     x
 *   +----->
 * y |
 *   |
 *   V
 */
@Suppress("unused")
data class Point(var x: Double = 0.0, var y: Double = 0.0) {
	constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

	/// The edges this point constitutes an upper ending point
	val edge_list by lazy { arrayListOf<Edge>() }

	/// Set this point to all zeros.
	fun set_zero(): Unit {
		this.x = 0.0
		this.y = 0.0
	}

	/// Set this point to some specified coordinates.
	fun set(x: Double, y: Double): Unit {
		this.x = x
		this.y = y
	}

	/// Negate this point.
	fun neg(): Unit {
		this.x = -this.x
		this.y = -this.y
	}

	/// Add a point to this point.
	fun add(v: Point): Unit {
		this.x += v.x
		this.y += v.y
	}

	/// Subtract a point from this point.
	fun sub(v: Point): Unit {
		this.x -= v.x
		this.y -= v.y
	}

	/// Multiply this point by a scalar.
	fun mul(s: Double): Unit {
		this.x *= s
		this.y *= s
	}

	val length: Double get() = Math.sqrt(x * x + y * y)

	/// Convert this point into a unit point. Returns the Length.
	fun normalize(): Double {
		val cachedLength: Double = this.length
		this.x /= cachedLength
		this.y /= cachedLength
		return cachedLength
	}

	fun equals(that: Point): Boolean = (this.x == that.x) && (this.y == that.y)

	companion object {
		fun getUniqueList(nonUniqueList: List<Point>): List<Point> = nonUniqueList.distinct()

		fun middle(a: Point, b: Point): Point {
			return Point((a.x + b.x) / 2, (a.y + b.y) / 2)
		}

		fun sortPoints(points: ArrayList<Point>): Unit {
			points.sortWith(Comparator({ l, r -> cmpPoints(l, r) }))
		}

		protected fun cmpPoints(l: Point, r: Point): Int {
			var ret: Double = l.y - r.y
			if (ret == 0.0) ret = l.x - r.x
			if (ret < 0) return -1
			if (ret > 0) return +1
			return 0
		}

	}

	override fun toString(): String = "Point(${x.niceStr}, ${y.niceStr})"
}

class Sweep(
	protected var context: SweepContext
) {
	/**
	 * Triangulate simple polygon with holes.
	 * @param   tcx SweepContext object.
	 */
	fun triangulate(): Unit {
		context.initTriangulation()
		context.createAdvancingFront()
		sweepPoints()                    // Sweep points; build mesh
		finalizationPolygon()            // Clean up
	}

	fun sweepPoints(): Unit {
		for (i in 1 until this.context.points.size) {
			val point: Point = this.context.points[i]
			val node: Node = this.pointEvent(point)
			for (j in 0 until point.edge_list.size) {
				this.edgeEventByEdge(point.edge_list[j], node)
			}
		}
	}

	fun finalizationPolygon(): Unit {
		// Get an Internal triangle to start with
		val next = this.context.front.head.next!!
		var t: Triangle = next.triangle!!
		val p: Point = next.point
		while (!t.getConstrainedEdgeCW(p)) t = t.neighborCCW(p)!!

		// Collect interior triangles constrained by edges
		this.context.meshClean(t)
	}

	/**
	 * Find closes node to the left of the point and
	 * create a triangle. If needed holes and basins
	 * will be filled to.
	 */
	fun pointEvent(point: Point): Node {
		val node = this.context.locateNode(point)!!
		val new_node = newFrontTriangle(point, node)

		// Only need to check +epsilon since point never have smaller
		// x value than node due to how we fetch nodes from the front
		if (point.x <= (node.point.x + Constants.EPSILON)) fill(node)

		//tcx.AddNode(new_node);

		fillAdvancingFront(new_node)
		return new_node
	}

	fun edgeEventByEdge(edge: Edge, node: Node): Unit {
		val edge_event = this.context.edge_event
		edge_event.constrained_edge = edge
		edge_event.right = (edge.p.x > edge.q.x)

		val triangle = node.triangle!!

		if (triangle.isEdgeSide(edge.p, edge.q)) return

		// For now we will do all needed filling
		// TODO: integrate with flip process might give some better performance
		//       but for now this avoid the issue with cases that needs both flips and fills
		this.fillEdgeEvent(edge, node)

		this.edgeEventByPoints(edge.p, edge.q, triangle, edge.q)
	}

	fun edgeEventByPoints(ep: Point, eq: Point, triangle: Triangle, point: Point): Unit {
		if (triangle.isEdgeSide(ep, eq)) return

		val p1: Point = triangle.pointCCW(point)
		val o1: Orientation = Orientation.orient2d(eq, p1, ep)
		if (o1 == Orientation.COLLINEAR) throw(Error("Sweep.edgeEvent: Collinear not supported!"))

		val p2: Point = triangle.pointCW(point)
		val o2: Orientation = Orientation.orient2d(eq, p2, ep)
		if (o2 == Orientation.COLLINEAR) throw(Error("Sweep.edgeEvent: Collinear not supported!"))

		if (o1 == o2) {
			// Need to decide if we are rotating CW or CCW to get to a triangle
			// that will cross edge
			edgeEventByPoints(ep, eq, if (o1 == Orientation.CW) triangle.neighborCCW(point)!! else triangle.neighborCW(point)!!, point)
		} else {
			// This triangle crosses constraint so lets flippin start!
			flipEdgeEvent(ep, eq, triangle, point)
		}
	}

	fun newFrontTriangle(point: Point, node: Node): Node {
		val triangle: Triangle = Triangle(point, node.point, node.next!!.point)

		triangle.markNeighborTriangle(node.triangle!!)
		this.context.addToSet(triangle)

		val new_node: Node = Node(point)
		new_node.next = node.next
		new_node.prev = node
		node.next!!.prev = new_node
		node.next = new_node

		if (!legalize(triangle)) this.context.mapTriangleToNodes(triangle)

		return new_node
	}

	/**
	 * Adds a triangle to the advancing front to fill a hole.
	 * @param tcx
	 * @param node - middle node, that is the bottom of the hole
	 */
	fun fill(node: Node): Unit {
		val triangle: Triangle = Triangle(node.prev!!.point, node.point, node.next!!.point)

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
	fun fillAdvancingFront(n: Node): Unit {
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
	fun legalize(t: Triangle): Boolean {
		// To legalize a triangle we start by finding if any of the three edges
		// violate the Delaunay condition
		for (i in 0 until 3) {
			if (t.delaunay_edge[i]) continue
			val ot: Triangle = t.neighbors[i] ?: continue
			val p: Point = t.points[i]
			val op: Point = ot.oppositePoint(t, p)
			val oi: Int = ot.index(op)

			// If this is a Constrained Edge or a Delaunay Edge(only during recursive legalization)
			// then we should not try to legalize
			if (ot.constrained_edge[oi] || ot.delaunay_edge[oi]) {
				t.constrained_edge[i] = ot.constrained_edge[oi]
				continue
			}

			if (Utils.insideIncircle(p, t.pointCCW(p), t.pointCW(p), op)) {
				// Lets mark this shared edge as Delaunay
				t.delaunay_edge[i] = true
				ot.delaunay_edge[oi] = true

				// Lets rotate shared edge one vertex CW to legalize it
				Triangle.rotateTrianglePair(t, p, ot, op)

				// We now got one valid Delaunay Edge shared by two triangles
				// This gives us 4 edges to check for Delaunay

				var not_legalized: Boolean

				// Make sure that triangle to node mapping is done only one time for a specific triangle
				not_legalized = !this.legalize(t)
				if (not_legalized) this.context.mapTriangleToNodes(t)

				not_legalized = !this.legalize(ot)
				if (not_legalized) this.context.mapTriangleToNodes(ot)

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
	 * @param tcx
	 * @param node - starting node, this or next node will be left node
	 */
	fun fillBasin(node: Node): Unit {
		val context = this.context
		val basin = context.basin
		basin.left_node = if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CCW) node.next!!.next else node.next

		// Find the bottom and right node
		basin.bottom_node = basin.left_node
		while ((basin.bottom_node!!.next != null) && (basin.bottom_node!!.point.y >= basin.bottom_node!!.next!!.point.y)) {
			basin.bottom_node = basin.bottom_node!!.next
		}

		// No valid basin
		if (basin.bottom_node == basin.left_node) return

		basin.right_node = basin.bottom_node
		while ((basin.right_node!!.next != null) && (basin.right_node!!.point.y < basin.right_node!!.next!!.point.y)) {
			basin.right_node = basin.right_node!!.next
		}

		// No valid basins
		if (basin.right_node == basin.bottom_node) return

		basin.width = (basin.right_node!!.point.x - basin.left_node!!.point.x)
		basin.left_highest = (basin.left_node!!.point.y > basin.right_node!!.point.y)

		this.fillBasinReq(basin.bottom_node!!)
	}

	/**
	 * Recursive algorithm to fill a Basin with triangles
	 *
	 * @param tcx
	 * @param node - bottom_node
	 */
	fun fillBasinReq(node: Node): Unit {
		@Suppress("NAME_SHADOWING")
		var node = node
		// if shallow stop filling
		if (this.isShallow(node)) return

		this.fill(node)

		if (node.prev == this.context.basin.left_node && node.next == this.context.basin.right_node) {
			return
		} else if (node.prev == this.context.basin.left_node) {
			if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CW) return
			node = node.next!!
		} else if (node.next == this.context.basin.right_node) {
			if (Orientation.orient2d(node.point, node.prev!!.point, node.prev!!.prev!!.point) == Orientation.CCW) return
			node = node.prev!!
		} else {
			// Continue with the neighbor node with lowest Y value
			node = if (node.prev!!.point.y < node.next!!.point.y) node.prev!! else node.next!!
		}

		this.fillBasinReq(node)
	}

	fun isShallow(node: Node): Boolean {
		val height: Double = if (this.context.basin.left_highest) {
			this.context.basin.left_node!!.point.y - node.point.y
		} else {
			this.context.basin.right_node!!.point.y - node.point.y
		}

		// if shallow stop filling
		return (this.context.basin.width > height)
	}

	fun fillEdgeEvent(edge: Edge, node: Node): Unit {
		if (this.context.edge_event.right) {
			this.fillRightAboveEdgeEvent(edge, node)
		} else {
			this.fillLeftAboveEdgeEvent(edge, node)
		}
	}

	fun fillRightAboveEdgeEvent(edge: Edge, node: Node): Unit {
		var node = node
		while (node.next!!.point.x < edge.p.x) {
			// Check if next node is below the edge
			if (Orientation.orient2d(edge.q, node.next!!.point, edge.p) == Orientation.CCW) {
				this.fillRightBelowEdgeEvent(edge, node)
			} else {
				node = node.next!!
			}
		}
	}

	fun fillRightBelowEdgeEvent(edge: Edge, node: Node): Unit {
		if (node.point.x >= edge.p.x) return
		if (Orientation.orient2d(node.point, node.next!!.point, node.next!!.next!!.point) == Orientation.CCW) {
			// Concave
			this.fillRightConcaveEdgeEvent(edge, node)
		} else {
			this.fillRightConvexEdgeEvent(edge, node) // Convex
			this.fillRightBelowEdgeEvent(edge, node) // Retry this one
		}
	}

	fun fillRightConcaveEdgeEvent(edge: Edge, node: Node): Unit {
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

	fun fillRightConvexEdgeEvent(edge: Edge, node: Node): Unit {
		// Next concave or convex?
		if (Orientation.orient2d(node.next!!.point, node.next!!.next!!.point, node.next!!.next!!.next!!.point) == Orientation.CCW) {
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

	fun fillLeftAboveEdgeEvent(edge: Edge, node: Node): Unit {
		var node = node
		while (node.prev!!.point.x > edge.p.x) {
			// Check if next node is below the edge
			if (Orientation.orient2d(edge.q, node.prev!!.point, edge.p) == Orientation.CW) {
				this.fillLeftBelowEdgeEvent(edge, node)
			} else {
				node = node.prev!!
			}
		}
	}

	fun fillLeftBelowEdgeEvent(edge: Edge, node: Node): Unit {
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

	fun fillLeftConvexEdgeEvent(edge: Edge, node: Node): Unit {
		// Next concave or convex?
		if (Orientation.orient2d(node.prev!!.point, node.prev!!.prev!!.point, node.prev!!.prev!!.prev!!.point) == Orientation.CW) {
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

	fun fillLeftConcaveEdgeEvent(edge: Edge, node: Node): Unit {
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

	fun flipEdgeEvent(ep: Point, eq: Point, t: Triangle, p: Point): Unit {
		var t = t
		val ot: Triangle = t.neighborAcross(p) ?: throw Error("[BUG:FIXME] FLIP failed due to missing triangle!")
		// If we want to integrate the fillEdgeEvent do it here
		// With current implementation we should never get here

		val op: Point = ot.oppositePoint(t, p)

		if (Utils.inScanArea(p, t.pointCCW(p), t.pointCW(p), op)) {
			// Lets rotate shared edge one vertex CW
			Triangle.rotateTrianglePair(t, p, ot, op)
			this.context.mapTriangleToNodes(t)
			this.context.mapTriangleToNodes(ot)

			// @TODO: equals?
			if ((p == eq) && (op == ep)) {
				if ((eq == this.context.edge_event.constrained_edge!!.q) && (ep == this.context.edge_event.constrained_edge!!.p)) {
					t.markConstrainedEdgeByPoints(ep, eq)
					ot.markConstrainedEdgeByPoints(ep, eq)
					this.legalize(t)
					this.legalize(ot)
				} else {
					// XXX: I think one of the triangles should be legalized here?
				}
			} else {
				val o: Orientation = Orientation.orient2d(eq, op, ep)
				t = this.nextFlipTriangle(o, t, ot, p, op)
				this.flipEdgeEvent(ep, eq, t, p)
			}
		} else {
			val newP: Point = Sweep.nextFlipPoint(ep, eq, ot, op)
			this.flipScanEdgeEvent(ep, eq, t, ot, newP)
			this.edgeEventByPoints(ep, eq, t, p)
		}
	}

	fun nextFlipTriangle(o: Orientation, t: Triangle, ot: Triangle, p: Point, op: Point): Triangle {
		if (o == Orientation.CCW) {
			// ot is not crossing edge after flip
			ot.delaunay_edge[ot.edgeIndex(p, op)] = true
			this.legalize(ot)
			ot.clearDelunayEdges()
			return t
		} else {
			// t is not crossing edge after flip
			t.delaunay_edge[t.edgeIndex(p, op)] = true
			this.legalize(t)
			t.clearDelunayEdges()
			return ot
		}
	}

	companion object {
		fun nextFlipPoint(ep: Point, eq: Point, ot: Triangle, op: Point): Point {
			return when (Orientation.orient2d(eq, op, ep)) {
				Orientation.CW -> ot.pointCCW(op) // Right
				Orientation.CCW -> ot.pointCW(op) // Left
				else -> throw Error("[Unsupported] Sweep.NextFlipPoint: opposing point on constrained edge!")
			}
		}
	}

	fun flipScanEdgeEvent(ep: Point, eq: Point, flip_triangle: Triangle, t: Triangle, p: Point): Unit {
		val ot = t.neighborAcross(p) ?: throw Error("[BUG:FIXME] FLIP failed due to missing triangle") // If we want to integrate the fillEdgeEvent do it here With current implementation we should never get here

		val op = ot.oppositePoint(t, p)

		if (Utils.inScanArea(eq, flip_triangle.pointCCW(eq), flip_triangle.pointCW(eq), op)) {
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
			val newP: Point = nextFlipPoint(ep, eq, ot, op)
			this.flipScanEdgeEvent(ep, eq, flip_triangle, ot, newP)
		}
	}
}

class SweepContext() {
	var triangles: ArrayList<Triangle> = ArrayList<Triangle>()
	var points: ArrayList<Point> = ArrayList<Point>()
	var edge_list: ArrayList<Edge> = ArrayList<Edge>()

	val set = LinkedHashSet<Triangle>()

	lateinit var front: AdvancingFront
	lateinit var head: Point
	lateinit var tail: Point

	lateinit var af_head: Node
	lateinit var af_middle: Node
	lateinit var af_tail: Node

	val basin: Basin = Basin()
	var edge_event: EdgeEvent = EdgeEvent()

	constructor(polyline: List<Point>) : this() {
		this.addPolyline(polyline)
	}

	protected fun addPoints(points: List<Point>): Unit {
		for (point in points) this.points.add(point)
	}

	fun addPolyline(polyline: List<Point>): Unit {
		this.initEdges(polyline)
		this.addPoints(polyline)
	}

	/**
	 * An alias of addPolyline.
	 *
	 * @param    polyline
	 */
	fun addHole(polyline: ArrayList<Point>): Unit {
		addPolyline(polyline)
	}

	protected fun initEdges(polyline: List<Point>): Unit {
		for (n in 0 until polyline.size) {
			this.edge_list.add(Edge(polyline[n], polyline[(n + 1) % polyline.size]))
		}
	}

	fun addToSet(triangle: Triangle): Unit {
		this.set += triangle
	}

	fun initTriangulation(): Unit {
		var xmin: Double = this.points[0].x
		var xmax: Double = this.points[0].x
		var ymin: Double = this.points[0].y
		var ymax: Double = this.points[0].y

		// Calculate bounds
		for (p in this.points) {
			if (p.x > xmax) xmax = p.x
			if (p.x < xmin) xmin = p.x
			if (p.y > ymax) ymax = p.y
			if (p.y < ymin) ymin = p.y
		}

		val dx: Double = Constants.kAlpha * (xmax - xmin)
		val dy: Double = Constants.kAlpha * (ymax - ymin)
		this.head = Point(xmax + dx, ymin - dy)
		this.tail = Point(xmin - dy, ymin - dy)

		// Sort points along y-axis
		Point.sortPoints(this.points)
		//throw(Error("@TODO Implement 'Sort points along y-axis' @see class SweepContext"));
	}

	fun locateNode(point: Point): Node? = this.front.locateNode(point.x)

	fun createAdvancingFront(): Unit {
		// Initial triangle
		val triangle: Triangle = Triangle(this.points[0], this.tail, this.head)

		addToSet(triangle)

		val head: Node = Node(triangle.points[1], triangle)
		val middle: Node = Node(triangle.points[0], triangle)
		val tail: Node = Node(triangle.points[2])

		this.front = AdvancingFront(head, tail)

		head.next = middle
		middle.next = tail
		middle.prev = head
		tail.prev = middle
	}

	fun removeNode(node: Node): Unit {
		// do nothing
	}

	fun mapTriangleToNodes(triangle: Triangle): Unit {
		for (n in 0 until 3) {
			if (triangle.neighbors[n] == null) {
				val neighbor: Node? = this.front.locatePoint(triangle.pointCW(triangle.points[n]))
				if (neighbor != null) neighbor.triangle = triangle
			}
		}
	}

	fun removeFromMap(triangle: Triangle): Unit {
		this.set -= triangle
	}

	fun meshClean(triangle: Triangle?, level: Int = 0): Unit {
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

class Triangle(
	p1: Point,
	p2: Point,
	p3: Point,
	fixOrientation: Boolean = false,
	checkOrientation: Boolean = true
) {
	// Triangle points
	//var points = arrayOfNulls<Point>(3) // [null, null, null]
	var points = Array<Point>(3) { Point(0.0, 0.0) } // [null, null, null]

	// Neighbor list
	var neighbors = arrayOfNulls<Triangle>(3) // [null, null, null]

	// Has this triangle been marked as an interior triangle?
	var interior: Boolean = false

	// Flags to determine if an edge is a Constrained edge
	var constrained_edge = Array<Boolean>(3) { false } // [false, false, false]

	// Flags to determine if an edge is a Delauney edge
	var delaunay_edge = Array<Boolean>(3) { false } // [false, false, false]

	init {
		@Suppress("NAME_SHADOWING")
		var p2 = p2
		@Suppress("NAME_SHADOWING")
		var p3 = p3
		if (fixOrientation) {
			if (Orientation.orient2d(p1, p2, p3) == Orientation.CW) {
				val pt = p3
				p3 = p2
				p2 = pt
				//println("Fixed orientation");
			}
		}
		if (checkOrientation && Orientation.orient2d(p3, p2, p1) != Orientation.CW) throw(Error("Triangle must defined with Orientation.CW"))
		this.points[0] = p1
		this.points[1] = p2
		this.points[2] = p3
	}

	/**
	 * Test if this Triangle contains the Point object given as parameter as its vertices.
	 *
	 * @return <code>True</code> if the Point objects are of the Triangle's vertices,
	 *         <code>false</code> otherwise.
	 */
	fun containsPoint(point: Point): Boolean = point == (points[0]) || point == (points[1]) || point == (points[2])

	/**
	 * Test if this Triangle contains the Edge object given as parameters as its bounding edges.
	 * @return <code>True</code> if the Edge objects are of the Triangle's bounding
	 *         edges, <code>false</code> otherwise.
	 */
	// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
	fun containsEdge(edge: Edge): Boolean = containsEdgePoints(edge.p, edge.q)

	// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
	fun containsEdgePoints(p1: Point, p2: Point): Boolean = containsPoint(p1) && containsPoint(p2)

	/**
	 * Update neighbor pointers.<br>
	 * This method takes either 3 parameters (<code>p1</code>, <code>p2</code> and
	 * <code>t</code>) or 1 parameter (<code>t</code>).
	 * @param   t   Triangle object.
	 * @param   p1  Point object.
	 * @param   p2  Point object.
	 */
	fun markNeighbor(t: Triangle, p1: Point, p2: Point): Unit {
		if ((p1 == (this.points[2]) && p2 == (this.points[1])) || (p1 == (this.points[1]) && p2 == (this.points[2]))) {
			this.neighbors[0] = t
			return
		}
		if ((p1 == (this.points[0]) && p2 == (this.points[2])) || (p1 == (this.points[2]) && p2 == (this.points[0]))) {
			this.neighbors[1] = t
			return
		}
		if ((p1 == (this.points[0]) && p2 == (this.points[1])) || (p1 == (this.points[1]) && p2 == (this.points[0]))) {
			this.neighbors[2] = t
			return
		}
		throw Error("Invalid markNeighbor call (1)!")
	}

	fun markNeighborTriangle(that: Triangle): Unit {
		// exhaustive search to update neighbor pointers
		if (that.containsEdgePoints(this.points[1], this.points[2])) {
			this.neighbors[0] = that
			that.markNeighbor(this, this.points[1], this.points[2])
			return
		}

		if (that.containsEdgePoints(this.points[0], this.points[2])) {
			this.neighbors[1] = that
			that.markNeighbor(this, this.points[0], this.points[2])
			return
		}

		if (that.containsEdgePoints(this.points[0], this.points[1])) {
			this.neighbors[2] = that
			that.markNeighbor(this, this.points[0], this.points[1])
			return
		}
	}

	/*public fun getPointIndexOffset(p:Point, offset:Int = 0):uint {
		for (var n:uint = 0; n < 3; n++) if (p == (this.points[n])) return (n + offset) % 3;
		throw(Error("Point not in triangle"));
	}*/

	// Optimized?
	fun getPointIndexOffset(p: Point, offset: Int = 0): Int {
		var no: Int = offset
		for (n in 0 until 3) {
			while (no < 0) no += 3
			while (no > 2) no -= 3
			if (p == (this.points[n])) return no
			no++
		}
		throw Error("Point not in triangle")
	}

	/**
	 * Alias for containsPoint
	 *
	 * @param    p
	 * @return
	 */
	fun isPointAVertex(p: Point): Boolean = containsPoint(p)
	//for (var n:uint = 0; n < 3; n++) if (p == [this.points[n]]) return true;
	//return false;

	/**
	 * Return the point clockwise to the given point.
	 * Return the point counter-clockwise to the given point.
	 *
	 * Return the neighbor clockwise to given point.
	 * Return the neighbor counter-clockwise to given point.
	 */

	//private const CCW_OFFSET:Int = +1;
	//private const CW_OFFSET:Int = -1;

	fun pointCW(p: Point): Point = this.points[getPointIndexOffset(p, CCW_OFFSET)]

	fun pointCCW(p: Point): Point = this.points[getPointIndexOffset(p, CW_OFFSET)]
	fun neighborCW(p: Point): Triangle? = this.neighbors[getPointIndexOffset(p, CW_OFFSET)]
	fun neighborCCW(p: Point): Triangle? = this.neighbors[getPointIndexOffset(p, CCW_OFFSET)]

	fun getConstrainedEdgeCW(p: Point): Boolean = this.constrained_edge[getPointIndexOffset(p, CW_OFFSET)]
	fun setConstrainedEdgeCW(p: Point, ce: Boolean): Boolean = ce.also { this.constrained_edge[getPointIndexOffset(p, CW_OFFSET)] = ce }
	fun getConstrainedEdgeCCW(p: Point): Boolean = this.constrained_edge[getPointIndexOffset(p, CCW_OFFSET)]
	fun setConstrainedEdgeCCW(p: Point, ce: Boolean): Boolean = ce.also { this.constrained_edge[getPointIndexOffset(p, CCW_OFFSET)] = ce }
	fun getDelaunayEdgeCW(p: Point): Boolean = this.delaunay_edge[getPointIndexOffset(p, CW_OFFSET)]
	fun setDelaunayEdgeCW(p: Point, e: Boolean): Boolean = e.also { this.delaunay_edge[getPointIndexOffset(p, CW_OFFSET)] = e }
	fun getDelaunayEdgeCCW(p: Point): Boolean = this.delaunay_edge[getPointIndexOffset(p, CCW_OFFSET)]
	fun setDelaunayEdgeCCW(p: Point, e: Boolean): Boolean = e.also { this.delaunay_edge[getPointIndexOffset(p, CCW_OFFSET)] = e }

	/**
	 * The neighbor across to given point.
	 */
	fun neighborAcross(p: Point): Triangle? = this.neighbors[getPointIndexOffset(p, 0)]

	fun oppositePoint(t: Triangle, p: Point): Point = this.pointCW(t.pointCW(p))

	/**
	 * Legalize triangle by rotating clockwise.<br>
	 * This method takes either 1 parameter (then the triangle is rotated around
	 * points(0)) or 2 parameters (then the triangle is rotated around the first
	 * parameter).
	 */
	fun legalize(opoint: Point, npoint: Point? = null): Unit {
		if (npoint == null) return this.legalize(this.points[0], opoint)

		if (opoint == this.points[0]) {
			this.points[1] = this.points[0]
			this.points[0] = this.points[2]
			this.points[2] = npoint
		} else if (opoint == this.points[1]) {
			this.points[2] = this.points[1]
			this.points[1] = this.points[0]
			this.points[0] = npoint
		} else if (opoint == this.points[2]) {
			this.points[0] = this.points[2]
			this.points[2] = this.points[1]
			this.points[1] = npoint
		} else {
			throw Error("Invalid js.poly2tri.Triangle.Legalize call!")
		}
	}

	/**
	 * Alias for getPointIndexOffset
	 *
	 * @param    p
	 */
	// @TODO: Do not use exceptions
	fun index(p: Point): Int = try {
		this.getPointIndexOffset(p, 0)
	} catch (e: Throwable) {
		-1
	}

	fun edgeIndex(p1: Point, p2: Point): Int {
		if (p1 == this.points[0]) {
			if (p2 == this.points[1]) return 2
			if (p2 == this.points[2]) return 1
		} else if (p1 == this.points[1]) {
			if (p2 == this.points[2]) return 0
			if (p2 == this.points[0]) return 2
		} else if (p1 == this.points[2]) {
			if (p2 == this.points[0]) return 1
			if (p2 == this.points[1]) return 0
		}
		return -1
	}


	/**
	 * Mark an edge of this triangle as constrained.<br>
	 * This method takes either 1 parameter (an edge index or an Edge instance) or
	 * 2 parameters (two Point instances defining the edge of the triangle).
	 */
	fun markConstrainedEdgeByIndex(index: Int): Unit = run { this.constrained_edge[index] = true }

	fun markConstrainedEdgeByEdge(edge: Edge): Unit = this.markConstrainedEdgeByPoints(edge.p, edge.q)

	fun markConstrainedEdgeByPoints(p: Point, q: Point): Unit {
		if ((q == (this.points[0]) && p == (this.points[1])) || (q == (this.points[1]) && p == (this.points[0]))) {
			this.constrained_edge[2] = true
		} else if ((q == (this.points[0]) && p == (this.points[2])) || (q == (this.points[2]) && p == (this.points[0]))) {
			this.constrained_edge[1] = true
		} else if ((q == (this.points[1]) && p == (this.points[2])) || (q == (this.points[2]) && p == (this.points[1]))) {
			this.constrained_edge[0] = true
		}
	}

	// isEdgeSide
	/**
	 * Checks if a side from this triangle is an edge side.
	 * If sides are not marked they will be marked.
	 *
	 * @param    ep
	 * @param    eq
	 * @return
	 */
	fun isEdgeSide(ep: Point, eq: Point): Boolean {
		val index = this.edgeIndex(ep, eq)
		if (index == -1) return false
		this.markConstrainedEdgeByIndex(index)
		this.neighbors[index]?.markConstrainedEdgeByPoints(ep, eq)
		return true
	}

	fun clearNeigbors(): Unit {
		this.neighbors[0] = null
		this.neighbors[1] = null
		this.neighbors[2] = null
	}

	fun clearDelunayEdges(): Unit {
		this.delaunay_edge[0] = false
		this.delaunay_edge[1] = false
		this.delaunay_edge[2] = false
	}

	fun equals(that: Triangle): Boolean = Arrays.equals(this.points, that.points)

	fun pointInsideTriangle(pp: Point): Boolean {
		val p1: Point = points[0]
		val p2: Point = points[1]
		val p3: Point = points[2]
		if (_product(p1, p2, p3) >= 0) {
			return (_product(p1, p2, pp) >= 0) && (_product(p2, p3, pp)) >= 0 && (_product(p3, p1, pp) >= 0)
		} else {
			return (_product(p1, p2, pp) <= 0) && (_product(p2, p3, pp)) <= 0 && (_product(p3, p1, pp) <= 0)
		}
	}

	override fun toString(): String = "Triangle(${this.points[0]}, ${this.points[1]}, ${this.points[2]})"

	companion object {
		private const val CW_OFFSET: Int = +1
		private const val CCW_OFFSET: Int = -1

		fun getNotCommonVertexIndex(t1: Triangle, t2: Triangle): Int {
			var sum: Int = 0
			var index: Int = -1
			if (!t2.containsPoint(t1.points[0])) {
				index = 0
				sum++
			}
			if (!t2.containsPoint(t1.points[1])) {
				index = 1
				sum++
			}
			if (!t2.containsPoint(t1.points[2])) {
				index = 2
				sum++
			}
			if (sum != 1) throw Error("Triangles are not contiguous")
			return index
		}

		fun getNotCommonVertex(t1: Triangle, t2: Triangle): Point = t1.points[getNotCommonVertexIndex(t1, t2)]

		fun getCommonEdge(t1: Triangle, t2: Triangle): Edge {
			val commonIndexes = ArrayList<Point>()
			for (point in t1.points) if (t2.containsPoint(point)) commonIndexes.add(point)
			if (commonIndexes.size != 2) throw Error("Triangles are not contiguous")
			return Edge(commonIndexes[0], commonIndexes[1])
		}


		/**
		 * Rotates a triangle pair one vertex CW
		 *<pre>
		 *       n2                    n2
		 *  P +-----+             P +-----+
		 *    | t  /|               |\  t |
		 *    |   / |               | \   |
		 *  n1|  /  |n3           n1|  \  |n3
		 *    | /   |    after CW   |   \ |
		 *    |/ oT |               | oT \|
		 *    +-----+ oP            +-----+
		 *       n4                    n4
		 * </pre>
		 */
		fun rotateTrianglePair(t: Triangle, p: Point, ot: Triangle, op: Point): Unit {
			val n1 = t.neighborCCW(p)
			val n2 = t.neighborCW(p)
			val n3 = ot.neighborCCW(op)
			val n4 = ot.neighborCW(op)

			val ce1 = t.getConstrainedEdgeCCW(p)
			val ce2 = t.getConstrainedEdgeCW(p)
			val ce3 = ot.getConstrainedEdgeCCW(op)
			val ce4 = ot.getConstrainedEdgeCW(op)

			val de1 = t.getDelaunayEdgeCCW(p)
			val de2 = t.getDelaunayEdgeCW(p)
			val de3 = ot.getDelaunayEdgeCCW(op)
			val de4 = ot.getDelaunayEdgeCW(op)

			t.legalize(p, op)
			ot.legalize(op, p)

			// Remap delaunay_edge
			ot.setDelaunayEdgeCCW(p, de1)
			t.setDelaunayEdgeCW(p, de2)
			t.setDelaunayEdgeCCW(op, de3)
			ot.setDelaunayEdgeCW(op, de4)

			// Remap constrained_edge
			ot.setConstrainedEdgeCCW(p, ce1)
			t.setConstrainedEdgeCW(p, ce2)
			t.setConstrainedEdgeCCW(op, ce3)
			ot.setConstrainedEdgeCW(op, ce4)

			// Remap neighbors
			// XXX: might optimize the markNeighbor by keeping track of
			//      what side should be assigned to what neighbor after the
			//      rotation. Now mark neighbor does lots of testing to find
			//      the right side.
			t.clearNeigbors()
			ot.clearNeigbors()
			if (n1 != null) ot.markNeighborTriangle(n1)
			if (n2 != null) t.markNeighborTriangle(n2)
			if (n3 != null) t.markNeighborTriangle(n3)
			if (n4 != null) ot.markNeighborTriangle(n4)
			t.markNeighborTriangle(ot)
		}

		fun getUniquePointsFromTriangles(triangles: ArrayList<Triangle>) = Point.getUniqueList(triangles.flatMap { it.points.toList() })

		fun traceList(triangles: ArrayList<Triangle>): Unit {
			val pointsList = Triangle.getUniquePointsFromTriangles(triangles)
			val pointsMap = hashMapOf<Point, Int>()
			var points_length: Int = 0
			for (point in pointsList) pointsMap[point] = ++points_length
			fun getPointName(point: Point): String = "p" + pointsMap[point]
			println("Points:")
			for (point in pointsList) println("  " + getPointName(point) + " = " + point)
			println("Triangles:")
			for (triangle in triangles) println("  Triangle(${getPointName(triangle.points[0])}, ${getPointName(triangle.points[1])}, ${getPointName(triangle.points[2])})")
		}

		private fun _product(p1: Point, p2: Point, p3: Point): Double = (p1.x - p3.x) * (p2.y - p3.y) - (p1.y - p3.y) * (p2.x - p3.x)

	}
}

object Utils {
	/**
	 * <b>Requirement</b>:<br>
	 * 1. a, b and c form a triangle.<br>
	 * 2. a and d is know to be on opposite side of bc<br>
	 * <pre>
	 *                a
	 *                +
	 *               / \
	 *              /   \
	 *            b/     \c
	 *            +-------+
	 *           /    d    \
	 *          /           \
	 * </pre>
	 * <b>Fact</b>: d has to be in area B to have a chance to be inside the circle formed by
	 *  a,b and c<br>
	 *  d is outside B if orient2d(a,b,d) or orient2d(c,a,d) is CW<br>
	 *  This preknowledge gives us a way to optimize the incircle test
	 * @param pa - triangle point, opposite d
	 * @param pb - triangle point
	 * @param pc - triangle point
	 * @param pd - point opposite a
	 * @return true if d is inside circle, false if on circle edge
	 */
	fun insideIncircle(pa: Point, pb: Point, pc: Point, pd: Point): Boolean {
		val adx: Double = pa.x - pd.x
		val ady: Double = pa.y - pd.y
		val bdx: Double = pb.x - pd.x
		val bdy: Double = pb.y - pd.y

		val adxbdy: Double = adx * bdy
		val bdxady: Double = bdx * ady
		val oabd: Double = adxbdy - bdxady

		if (oabd <= 0) return false

		val cdx: Double = pc.x - pd.x
		val cdy: Double = pc.y - pd.y

		val cdxady: Double = cdx * ady
		val adxcdy: Double = adx * cdy
		val ocad: Double = cdxady - adxcdy

		if (ocad <= 0) return false

		val bdxcdy: Double = bdx * cdy
		val cdxbdy: Double = cdx * bdy

		val alift: Double = adx * adx + ady * ady
		val blift: Double = bdx * bdx + bdy * bdy
		val clift: Double = cdx * cdx + cdy * cdy

		val det: Double = alift * (bdxcdy - cdxbdy) + blift * ocad + clift * oabd
		return det > 0
	}

	fun inScanArea(pa: Point, pb: Point, pc: Point, pd: Point): Boolean {
		val pdx: Double = pd.x
		val pdy: Double = pd.y
		val adx: Double = pa.x - pdx
		val ady: Double = pa.y - pdy
		val bdx: Double = pb.x - pdx
		val bdy: Double = pb.y - pdy

		val adxbdy: Double = adx * bdy
		val bdxady: Double = bdx * ady
		val oabd: Double = adxbdy - bdxady

		if (oabd <= Constants.EPSILON) return false

		val cdx: Double = pc.x - pdx
		val cdy: Double = pc.y - pdy

		val cdxady: Double = cdx * ady
		val adxcdy: Double = adx * cdy
		val ocad: Double = cdxady - adxcdy

		if (ocad <= Constants.EPSILON) return false

		return true
	}
}

