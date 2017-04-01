package com.soywiz.korma.geom

import com.soywiz.korma.Vector2

typealias Point2d = Vector2

// @TODO: Check if this avoid boxing!
inline fun Point2d(x: Number, y: Number) = Point2d(x.toDouble(), y.toDouble())

fun Iterable<Point2d>.getPolylineLength(): Double {
	var out = 0.0
	var prev: Point2d? = null
	for (cur in this) {
		if (prev != null) out += prev.distanceTo(cur)
		prev = cur
	}
	return out
}