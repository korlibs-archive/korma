package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.clipper.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.geom.triangle.*
import com.soywiz.korma.geom.vector.*

fun BoundsBuilder.add(shape: Shape2d) {
    for (path in shape.paths) add(path)
}

val Shape2d.bounds: Rectangle get() = BoundsBuilder().apply { add(this@bounds) }.getBounds()

fun Rectangle.toShape() = Shape2d.Rectangle(x, y, width, height)

infix fun Shape2d.collidesWith(other: Shape2d): Boolean =
    this.clipperOp(other, Clipper.ClipType.INTERSECTION) != Shape2d.Empty

infix fun Shape2d.intersection(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.INTERSECTION)
infix fun Shape2d.union(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.UNION)
infix fun Shape2d.xor(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.XOR)
infix fun Shape2d.difference(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.DIFFERENCE)

operator fun Shape2d.plus(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.UNION)
operator fun Shape2d.minus(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.DIFFERENCE)

fun Shape2d.extend(size: Double, cap: VectorPath.LineCap = VectorPath.LineCap.ROUND): Shape2d {
    val clipper = ClipperOffset()
    val solution = Paths()
    clipper.addPaths(
        this.paths.toClipperPaths(), Clipper.JoinType.MITER,
        if (this.closed) Clipper.EndType.CLOSED_POLYGON else cap.toClipper()
    )
    clipper.execute(solution, size)
    return solution.toShape2d()
}

fun Shape2d.extendLine(size: Double, join: VectorPath.LineJoin = VectorPath.LineJoin.SQUARE, cap: VectorPath.LineCap = VectorPath.LineCap.SQUARE): Shape2d {
    val clipper = ClipperOffset()
    val solution = Paths()
    clipper.addPaths(
        this.paths.toClipperPaths(), join.toClipper(),
        if (this.closed) Clipper.EndType.CLOSED_LINE else cap.toClipper()
    )
    clipper.execute(solution, size)
    return solution.toShape2d()
}

// @TODO: Instead of use curveSteps, let's determine the maximum distance between points for the curve, or the maximum angle (so we have a quality factor instead)
inline fun VectorPath.toPaths2(flush: () -> Unit, emit: (x: Double, y: Double) -> Unit, curveSteps: Int = 20) {
    var lx = 0.0
    var ly = 0.0
    flush()
    this.visitCmds(
        moveTo = { x, y ->
            //kotlin.io.println("moveTo")
            emit(x, y)
            lx = x
            ly = y
        },
        lineTo = { x, y ->
            //kotlin.io.println("lineTo")
            emit(x, y)
            lx = x
            ly = y
        },
        quadTo = { x0, y0, x1, y1 ->
            //kotlin.io.println("quadTo")
            val dt = 1.0 / curveSteps
            for (n in 1 until curveSteps) {
                Bezier.quadCalc(lx, ly, x0, y0, x1, y1, n * dt, emit)
            }
            lx = x1
            ly = y1
        },
        cubicTo = { x0, y0, x1, y1, x2, y2 ->
            //kotlin.io.println("cubicTo")
            val dt = 1.0 / curveSteps
            for (n in 1 until curveSteps) {
                Bezier.cubicCalc(lx, ly, x0, y0, x1, y1, x2, y2, n * dt, emit)
            }
            lx = x2
            ly = y2
        },
        close = {
            flush()
        }
    )
    flush()
}

fun VectorPath.toPaths2(): List<List<Point2d>> {
    val paths = arrayListOf<ArrayList<Point2d>>()
    var path = arrayListOf<Point2d>()
    toPaths2({
        if (path.isNotEmpty()) {
            paths += path
            path = ArrayList()
        }
    }, { x, y ->
        path.add(Point2d(x, y))
    })
    return paths
}

fun VectorPath.toShape2d(): Shape2d = this.toClipperPaths().toShape2d()

fun Shape2d.getAllPoints(out: PointArrayList = PointArrayList()): PointArrayList = out.apply { for (path in this@getAllPoints.paths) add(path) }
fun Shape2d.toPolygon(): Shape2d.Polygon = if (this is Shape2d.Polygon) this else Shape2d.Polygon(this.getAllPoints())

fun List<Point2d>.triangulate(): List<Triangle> {
    val sc = SweepContext(this)
    val s = Sweep(sc)
    s.triangulate()
    return sc.triangles.toList()
}

fun Shape2d.triangulate(): List<Triangle> = this.getAllPoints().toPoints().triangulate()

fun List<Point2d>.containsPoint(x: Double, y: Double): Boolean {
    var intersections = 0
    for (n in 0 until this.size - 1) {
        val p1 = this[n + 0]
        val p2 = this[n + 1]
        intersections += HorizontalLine.intersectionsWithLine(x, y, p1.x, p1.y, p2.x, p2.y)
    }
    return (intersections % 2) != 0
}
