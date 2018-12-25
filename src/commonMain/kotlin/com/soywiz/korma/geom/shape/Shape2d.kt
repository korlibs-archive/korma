package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.internal.*
import kotlin.math.*

abstract class Shape2d {
    abstract val paths: List<IPointArrayList>
    abstract val closed: Boolean
    open fun containsPoint(x: Double, y: Double) = false

    interface WithArea {
        val area: Double
    }

    object Empty : Shape2d(), WithArea {
        override val paths: List<PointArrayList> = listOf(PointArrayList(0))
        override val closed: Boolean = false
        override val area: Double = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Line(val x0: Double, val y0: Double, val x1: Double, val y1: Double) : Shape2d(), WithArea {
        companion object {
            inline operator fun invoke(x0: Number, y0: Number, x1: Number, y1: Number) = Line(x0.toDouble(), y0.toDouble(), x1.toDouble(), y1.toDouble())
        }

        override val paths get() = listOf(PointArrayList(2).apply { add(x0, y0).add(x1, y1) })
        override val closed: Boolean = false
        override val area: Double get() = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Circle(val x: Double, val y: Double, val radius: Double, val totalPoints: Int = 32) : Shape2d(), WithArea {
        companion object {
        	inline operator fun invoke(x: Number, y: Number, radius: Number, totalPoints: Int = 32) = Circle(x.toDouble(), y.toDouble(), radius.toDouble(), totalPoints)
        }

        override val paths by lazy {
            listOf(PointArrayList(totalPoints) {
                for (it in 0 until totalPoints) {
                    add(
                        x + Angle.cos01(it.toDouble() / totalPoints.toDouble()) * radius,
                        y + Angle.sin01(it.toDouble() / totalPoints.toDouble()) * radius
                    )
                }
            })
        }
        override val closed: Boolean = true
        override val area: Double get() = PI.toDouble() * radius * radius
        override fun containsPoint(x: Double, y: Double) = hypot(this.x - x, this.y - y) < radius
    }

    data class Rectangle(val rect: com.soywiz.korma.geom.Rectangle) : Shape2d(), WithArea, IRectangle by rect {
        companion object {
            inline operator fun invoke(x: Number, y: Number, width: Number, height: Number) = Rectangle(com.soywiz.korma.geom.Rectangle(x, y, width, height))
        }

        override val paths = listOf(PointArrayList(4) { add(x, y).add(x + width, y).add(x + width, y + height).add(x, y + height) })
        override val closed: Boolean = true
        override val area: Double get() = width * height
        override fun containsPoint(x: Double, y: Double) = (x in this.left..this.right) && (y in this.top..this.bottom)
        override fun toString(): String =
            "Rectangle(x=${x.niceStr}, y=${y.niceStr}, width=${width.niceStr}, height=${height.niceStr})"
    }

    data class Polygon(val points: IPointArrayList) : Shape2d() {
        override val paths = listOf(points)
        override val closed: Boolean = true
        override fun containsPoint(x: Double, y: Double): Boolean = this.points.contains(x, y)
    }

    data class Poyline(val points: IPointArrayList) : Shape2d(), WithArea {
        override val paths = listOf(points)
        override val closed: Boolean = false
        override val area: Double get() = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Complex(val items: List<Shape2d>) : Shape2d() {
        override val paths by lazy { items.flatMap { it.paths } }
        override val closed: Boolean = false
        override fun containsPoint(x: Double, y: Double): Boolean = this.getAllPoints().contains(x, y)
    }
}

val List<IPointArrayList>.totalVertices get() = this.map { it.size }.sum()

fun BoundsBuilder.add(shape: Shape2d) {
    for (path in shape.paths) add(path)
}

val Shape2d.bounds: Rectangle get() = BoundsBuilder().apply { add(this@bounds) }.getBounds()

fun Rectangle.toShape() = Shape2d.Rectangle(x, y, width, height)

// @TODO: Instead of use curveSteps, let's determine the maximum distance between points for the curve, or the maximum angle (so we have a quality factor instead)
inline fun VectorPath.emitPoints(flush: () -> Unit, emit: (x: Double, y: Double) -> Unit, curveSteps: Int = 20) {
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

fun VectorPath.toPathList(): List<IPointArrayList> {
    val paths = arrayListOf<IPointArrayList>()
    var path = PointArrayList()
    emitPoints({
        if (path.isNotEmpty()) {
            paths += path
            path = PointArrayList()
        }
    }, { x, y ->
        path.add(x, y)
    })
    return paths
}

fun Shape2d.getAllPoints(out: PointArrayList = PointArrayList()): PointArrayList = out.apply { for (path in this@getAllPoints.paths) add(path) }
fun Shape2d.toPolygon(): Shape2d.Polygon = if (this is Shape2d.Polygon) this else Shape2d.Polygon(this.getAllPoints())

fun List<IPoint>.containsPoint(x: Double, y: Double): Boolean {
    var intersections = 0
    for (n in 0 until this.size - 1) {
        val p1 = this[n + 0]
        val p2 = this[n + 1]
        intersections += HorizontalLine.intersectionsWithLine(x, y, p1.x, p1.y, p2.x, p2.y)
    }
    return (intersections % 2) != 0
}
