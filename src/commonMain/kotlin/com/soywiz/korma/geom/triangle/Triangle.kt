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

package com.soywiz.korma.geom.triangle

import com.soywiz.korma.geom.Orientation
import com.soywiz.korma.geom.Point2d
import com.soywiz.korma.geom.internal.*
import kotlin.math.abs

interface Triangle {
    val p0: Point2d
    val p1: Point2d
    val p2: Point2d

    data class Base(override val p0: Point2d, override val p1: Point2d, override val p2: Point2d) : Triangle

    companion object {
        fun area(p1: Point2d, p2: Point2d, p3: Point2d): Double = area(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)

        fun area(ax: Double, ay: Double, bx: Double, by: Double, cx: Double, cy: Double): Double {
            val a = bx - ax
            val b = by - ay
            val c = cx - ax
            val d = cy - ay
            return abs(a * d - c * b) / 2.0
        }

        fun getNotCommonVertexIndex(t1: Triangle, t2: Triangle): Int {
            var sum = 0
            var index: Int = -1
            if (!t2.containsPoint(t1.point(0))) {
                index = 0
                sum++
            }
            if (!t2.containsPoint(t1.point(1))) {
                index = 1
                sum++
            }
            if (!t2.containsPoint(t1.point(2))) {
                index = 2
                sum++
            }
            if (sum != 1) throw Error("Triangles are not contiguous")
            return index
        }

        fun getNotCommonVertex(t1: Triangle, t2: Triangle): Point2d = t1.point(getNotCommonVertexIndex(t1, t2))

        fun getUniquePointsFromTriangles(triangles: List<Triangle>) = triangles.flatMap { listOf(it.p0, it.p1, it.p2) }.distinct()

        fun traceList(triangles: List<Triangle>) {
            val pointsList = getUniquePointsFromTriangles(triangles)
            val pointsMap = hashMapOf<Point2d, Int>()
            var points_length = 0
            for (point in pointsList) pointsMap[point] = ++points_length
            fun getPointName(point: Point2d): String = "p" + pointsMap[point]
            println("Points:")
            for (point in pointsList) println("  " + getPointName(point) + " = " + point)
            println("Triangles:")
            for (triangle in triangles) println(
                "  Triangle(${getPointName(triangle.point(0))}, ${getPointName(triangle.point(1))}, ${getPointName(
                    triangle.point(2)
                )})"
            )
        }


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
        fun insideIncircle(pa: Point2d, pb: Point2d, pc: Point2d, pd: Point2d): Boolean {
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

        fun inScanArea(pa: Point2d, pb: Point2d, pc: Point2d, pd: Point2d): Boolean {
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
}

fun Triangle.point(index: Int) = when (index) {
    0 -> p0
    1 -> p1
    2 -> p2
    else -> error("Invalid triangle point index $index")
}
/**
 * Test if this Triangle contains the Point2d object given as parameter as its vertices.
 *
 * @return <code>True</code> if the Point2d objects are of the Triangle's vertices,
 *         <code>false</code> otherwise.
 */
fun Triangle.containsPoint(point: Point2d): Boolean = (point == p0) || (point == p1) || (point == p2)
/**
 * Test if this Triangle contains the Edge object given as parameters as its bounding edges.
 * @return <code>True</code> if the Edge objects are of the Triangle's bounding
 *         edges, <code>false</code> otherwise.
 */
// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
fun Triangle.containsEdge(edge: Edge): Boolean = containsEdgePoints(edge.p, edge.q)

// In a triangle to check if contains and edge is enough to check if it contains the two vertices.
fun Triangle.containsEdgePoints(p1: Point2d, p2: Point2d): Boolean = containsPoint(p1) && containsPoint(p2)

private fun _product(p1: Point2d, p2: Point2d, p3: Point2d): Double =
    (p1.x - p3.x) * (p2.y - p3.y) - (p1.y - p3.y) * (p2.x - p3.x)

fun Triangle.pointInsideTriangle(pp: Point2d): Boolean = if (_product(p0, p1, p2) >= 0) {
    (_product(p0, p1, pp) >= 0) && (_product(p1, p2, pp)) >= 0 && (_product(p2, p0, pp) >= 0)
} else {
    (_product(p0, p1, pp) <= 0) && (_product(p1, p2, pp)) <= 0 && (_product(p2, p0, pp) <= 0)
}

// Optimized?
fun Triangle.getPointIndexOffsetNoThrow(p: Point2d, offset: Int = 0, notFound: Int = Int.MIN_VALUE): Int {
    var no: Int = offset
    for (n in 0 until 3) {
        while (no < 0) no += 3
        while (no > 2) no -= 3
        if (p == (this.point(n))) return no
        no++
    }
    return notFound
}

fun Triangle.getPointIndexOffset(p: Point2d, offset: Int = 0): Int {
    val v = getPointIndexOffsetNoThrow(p, offset, Int.MIN_VALUE)
    if (v == Int.MIN_VALUE) throw Error("Point2d not in triangle")
    return v
}

fun Triangle.pointCW(p: Point2d): Point2d = this.point(getPointIndexOffset(p, -1))
fun Triangle.pointCCW(p: Point2d): Point2d = this.point(getPointIndexOffset(p, +1))
fun Triangle.oppositePoint(t: Triangle, p: Point2d): Point2d = this.pointCW(t.pointCW(p))

fun Triangle(p0: Point2d, p1: Point2d, p2: Point2d, fixOrientation: Boolean = false, checkOrientation: Boolean = true): Triangle {
    @Suppress("NAME_SHADOWING")
    var p1 = p1
    @Suppress("NAME_SHADOWING")
    var p2 = p2
    if (fixOrientation) {
        if (Orientation.orient2d(p0, p1, p2) == Orientation.CW) {
            val pt = p2
            p2 = p1
            p1 = pt
            //println("Fixed orientation");
        }
    }
    if (checkOrientation && Orientation.orient2d(p2, p1, p0) != Orientation.CW) throw(Error("Triangle must defined with Orientation.CW"))
    return Triangle.Base(p0, p1, p2)
}

/*public fun getPointIndexOffset(p:Point2d, offset:Int = 0):uint {
	for (var n:uint = 0; n < 3; n++) if (p == (this.points[n])) return (n + offset) % 3;
	throw(Error("Point2d not in triangle"));
}*/


/** Alias for containsPoint */
fun Triangle.isPointAVertex(p: Point2d): Boolean = containsPoint(p)
//for (var n:uint = 0; n < 3; n++) if (p == [this.points[n]]) return true;
//return false;

val Triangle.area: Double get() = Triangle.area(p0, p1, p2)

/** Alias for getPointIndexOffset */
fun Triangle.index(p: Point2d): Int = this.getPointIndexOffsetNoThrow(p, 0, -1)

fun Triangle.edgeIndex(p1: Point2d, p2: Point2d): Int {
    when (p1) {
        this.point(0) -> {
            if (p2 == this.point(1)) return 2
            if (p2 == this.point(2)) return 1
        }
        this.point(1) -> {
            if (p2 == this.point(2)) return 0
            if (p2 == this.point(0)) return 2
        }
        this.point(2) -> {
            if (p2 == this.point(0)) return 1
            if (p2 == this.point(1)) return 0
        }
    }
    return -1
}
