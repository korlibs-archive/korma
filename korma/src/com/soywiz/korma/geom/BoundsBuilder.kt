package com.soywiz.korma.geom

import com.soywiz.korma.Vector2

class BoundsBuilder {
	private var count = 0
	private var xmin = 0.0
	private var xmax = 0.0
	private var ymin = 0.0
	private var ymax = 0.0

	fun reset() {
		count = 0
	}

	fun add(x: Double, y: Double) {
		if (count == 0) {
			xmin = x
			xmax = x
			ymin = y
			ymax = y
		}
		count++
		xmin = Math.min(xmin, x)
		xmax = Math.max(xmax, x)
		ymin = Math.min(ymin, y)
		ymax = Math.max(ymax, y)
	}

	fun add(p: Vector2) = add(p.x, p.y)

	fun add(rect: Rectangle) {
		add(rect.left, rect.top)
		add(rect.bottom, rect.right)
	}

	fun getBounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(xmin, ymin, xmax, ymax)
}
