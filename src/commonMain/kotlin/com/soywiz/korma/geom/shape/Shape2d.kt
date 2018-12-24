package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.internal.*
import kotlin.math.*

abstract class Shape2d {
    abstract val paths: List<IPointArrayList>
    abstract val closed: Boolean
    abstract val area: Double
    open fun containsPoint(x: Double, y: Double) = false

    object Empty : Shape2d() {
        override val paths: List<PointArrayList> = listOf(PointArrayList(0))
        override val closed: Boolean = false
        override val area: Double = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Line(val x0: Double, val y0: Double, val x1: Double, val y1: Double) : Shape2d() {
        companion object {
            inline operator fun invoke(x0: Number, y0: Number, x1: Number, y1: Number) = Line(x0.toDouble(), y0.toDouble(), x1.toDouble(), y1.toDouble())
        }

        override val paths get() = listOf(PointArrayList(2).apply { add(x0, y0).add(x1, y1) })
        override val closed: Boolean = false
        override val area: Double get() = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Circle(val x: Double, val y: Double, val radius: Double, val totalPoints: Int = 32) : Shape2d() {
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
        override val area: Double get() = PI * radius * radius
        override fun containsPoint(x: Double, y: Double) = hypot(this.x - x, this.y - y) < radius
    }

    data class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double) : Shape2d() {
        companion object {
            inline operator fun invoke(x: Number, y: Number, width: Number, height: Number) = Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
        }

        val left: Double get() = x
        val top: Double get() = y
        val right: Double get() = x + width
        val bottom: Double get() = y + height
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
        override val area: Double by lazy { this.triangulate().sumByDouble { it.area } }
        override fun containsPoint(x: Double, y: Double): Boolean = this.points.contains(x, y)
    }

    data class Poyline(val points: IPointArrayList) : Shape2d() {
        override val paths = listOf(points)
        override val closed: Boolean = false
        override val area: Double get() = 0.0
        override fun containsPoint(x: Double, y: Double) = false
    }

    data class Complex(val items: List<Shape2d>) : Shape2d() {
        override val paths by lazy { items.flatMap { it.paths } }
        override val closed: Boolean = false
        override val area: Double by lazy { this.triangulate().sumByDouble { it.area } } // @TODO: Could we just sum stuff? Or maybe not because it could overlap?
        override fun containsPoint(x: Double, y: Double): Boolean = this.getAllPoints().contains(x, y)
    }
}

val List<IPointArrayList>.totalVertices get() = this.map { it.size }.sum()
