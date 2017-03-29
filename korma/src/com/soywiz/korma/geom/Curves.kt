package com.soywiz.korma.geom

//(x0,y0) is start point; (x1,y1),(x2,y2) is control points; (x3,y3) is end point.
object Curves {
	private val tvalues = DoubleArray(6)
	private val xvalues = DoubleArray(8)
	private val yvalues = DoubleArray(8)

	inline fun <T> quadToBezier(
		x0: Double, y0: Double, xc: Double, yc: Double, x1: Double, y1: Double,
		bezier: (x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) -> T
	): T {
		return bezier(
			x0, y0,
			x0 + 2 / 3 * (xc - x0), y0 + 2 / 3 * (yc - y0),
			x1 + 2 / 3 * (xc - x1), y1 + 2 / 3 * (yc - y1),
			x1, y1
		)
	}

	fun quadMinMax(x0: Double, y0: Double, xc: Double, yc: Double, x1: Double, y1: Double, target: Rectangle = Rectangle()): Rectangle {
		// http://fontforge.github.io/bezier.html
		//Any quadratic spline can be expressed as a cubic (where the cubic term is zero). The end points of the cubic will be the same as the quadratic's.
		//CP0 = QP0
		//CP3 = QP2
		//The two control points for the cubic are:
		//CP1 = QP0 + 2/3 *(QP1-QP0)
		//CP2 = QP2 + 2/3 *(QP1-QP2)

		//return bezierMinMax(x0, y0, xc, yc, xc, yc, x1, y1, target)

		return quadToBezier(x0, y0, xc, yc, x1, y1) { x0, y0, x1, y1, x2, y2, x3, y3 ->
			bezierMinMax(x0, y0, x1, y1, x2, y2, x3, y3, target)
		}
	}

	fun bezierMinMax(x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double, target: Rectangle = Rectangle()): Rectangle {
		var j = 0
		var a: Double
		var b: Double
		var c: Double
		var b2ac: Double
		var sqrtb2ac: Double
		for (i in 0 until 2) {
			if (i == 0) {
				b = 6 * x0 - 12 * x1 + 6 * x2
				a = -3 * x0 + 9 * x1 - 9 * x2 + 3 * x3
				c = 3 * x1 - 3 * x0
			} else {
				b = 6 * y0 - 12 * y1 + 6 * y2
				a = -3 * y0 + 9 * y1 - 9 * y2 + 3 * y3
				c = 3 * y1 - 3 * y0
			}
			if (Math.abs(a) < 1e-12) {
				if (Math.abs(b) >= 1e-12) {
					val t = -c / b
					if (0 < t && t < 1) tvalues[j++] = t
				}
			} else {
				b2ac = b * b - 4 * c * a
				if (b2ac < 0) continue
				sqrtb2ac = Math.sqrt(b2ac)
				val t1 = (-b + sqrtb2ac) / (2 * a)
				if (0 < t1 && t1 < 1) tvalues[j++] = t1
				val t2 = (-b - sqrtb2ac) / (2 * a)
				if (0 < t2 && t2 < 1) tvalues[j++] = t2
			}
		}

		while (j-- > 0) {
			val t = tvalues[j]
			val mt = 1 - t
			xvalues[j] = (mt * mt * mt * x0) + (3 * mt * mt * t * x1) + (3 * mt * t * t * x2) + (t * t * t * x3)
			yvalues[j] = (mt * mt * mt * y0) + (3 * mt * mt * t * y1) + (3 * mt * t * t * y2) + (t * t * t * y3)
		}

		xvalues[tvalues.size + 0] = x0
		xvalues[tvalues.size + 1] = x3
		yvalues[tvalues.size + 0] = y0
		yvalues[tvalues.size + 1] = y3

		return target.setBounds(xvalues.min() ?: 0.0, yvalues.min() ?: 0.0, xvalues.max() ?: 0.0, yvalues.max() ?: 0.0)
	}
}
