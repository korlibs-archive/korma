package com.soywiz.korma.geom

import com.soywiz.korma.Vector2

class BoundsBuilder {
	private var xmin = Double.MAX_VALUE
	private var xmax = Double.MIN_VALUE
	private var ymin = Double.MAX_VALUE
	private var ymax = Double.MIN_VALUE

	fun reset() {
		xmin = Double.MAX_VALUE
		xmax = Double.MIN_VALUE
		ymin = Double.MAX_VALUE
		ymax = Double.MIN_VALUE
	}

	fun add(x: Double, y: Double) {
		xmin = Math.min(xmin, x)
		xmax = Math.max(xmax, x)
		ymin = Math.min(ymin, y)
		ymax = Math.max(ymax, y)
		//println("add($x, $y) -> ($xmin,$ymin)-($xmax,$ymax)")
	}

	fun add(p: Vector2) = add(p.x, p.y)

	fun add(rect: Rectangle) {
		add(rect.left, rect.top)
		add(rect.bottom, rect.right)
	}

	fun getBounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(xmin, ymin, xmax, ymax)
}
