package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point2d
import de.lighti.clipper.DefaultClipper

interface Shape2d {
	val points: List<Point2d>
	val closed: Boolean
	val area: Double

	class Line(val x0: Double, val y0: Double, val x1: Double, val y1: Double) : Shape2d {
		override val points = listOf(Point2d(x0, y0), Point2d(x1, y1))
		override val closed: Boolean = false
		override val area: Double get() = 0.0
	}

	class Circle(val x: Double, val y: Double, val radius: Double, val totalPoints: Int = 32) : Shape2d {
		override val points = (0 until totalPoints).map {
			Point2d(
				x + Angle.cos01(it.toDouble() / totalPoints.toDouble()) * radius,
				y + Angle.sin01(it.toDouble() / totalPoints.toDouble()) * radius
			)
		}
		override val closed: Boolean = true
		override val area: Double get() = Math.PI * radius * radius
	}

	class Rectangle(val x: Double, val y: Double, val width: Double, val height: Double) : Shape2d {
		override val points = listOf(
			Point2d(x, y),
			Point2d(x + width, y),
			Point2d(x + width, y + height),
			Point2d(x, y + height)
		)
		override val closed: Boolean = true
		override val area: Double get() = width * height
	}

	class Polygon(override val points: List<Point2d>) : Shape2d {
		override val closed: Boolean = true
		override val area: Double get() = TODO()
	}

	class Poyline(override val points: List<Point2d>) : Shape2d {
		override val closed: Boolean = false
		override val area: Double get() = TODO()
	}
}

fun Shape2d.intersection(other: Shape2d): Shape2d {
	//val clipper = DefaultClipper()
	//clipper.addPath(other.points.map)
	TODO()
}