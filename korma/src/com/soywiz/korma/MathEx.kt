package com.soywiz.korma

import com.soywiz.korma.geom.PointInt
import com.soywiz.korma.interpolation.Interpolable

object MathEx {
	@JvmStatic fun divCeil(x: Int, y: Int): Int = 1 + ((x - 1) / y)
	@JvmStatic fun cos(value: Float): Float = java.lang.Math.cos(value.toDouble()).toFloat()
	@JvmStatic fun sin(value: Float): Float = java.lang.Math.sin(value.toDouble()).toFloat()
	@JvmStatic fun sqrt(value: Float): Float = java.lang.Math.sqrt(value.toDouble()).toFloat()

	@JvmStatic fun len(a: Double, b: Double) = Math.sqrt(a * a + b * b)
	@JvmStatic fun reinterpretIntFloat(value: Int): Float = java.lang.Float.intBitsToFloat(value)

	@JvmStatic fun interpolate(min: Int, max: Int, ratio: Double): Int = min + ((max - min) * ratio).toInt()
	@JvmStatic fun interpolate(min: Long, max: Long, ratio: Double) = min + ((max - min) * ratio).toLong()

	@JvmStatic fun <T : Interpolable<T>> interpolate(min: T, max: T, ratio: Double): T = min.interpolateWith(max, ratio)

	@JvmStatic fun interpolateAny(min: Any, max: Any, ratio: Double): Any {
		return when (min) {
			is Int -> interpolate(min, max as Int, ratio)
			is Long -> interpolate(min, max as Long, ratio)
			is Double -> interpolate(min, max as Double, ratio)
			is Vector2 -> min.setToInterpolated(min, max as Vector2, ratio)
			else -> throw RuntimeException("Unsupported interpolate with ${min.javaClass}")
		}
	}

	@JvmStatic fun min(a: Double, b: Double, c: Double, d: Double): Double = Math.min(Math.min(a, b), Math.min(c, d))
	@JvmStatic fun max(a: Double, b: Double, c: Double, d: Double): Double = Math.max(Math.max(a, b), Math.max(c, d))

	@JvmStatic fun clamp(v: Long, min: Long, max: Long): Long = if (v < min) min else if (v > max) max else v
	@JvmStatic fun clamp(v: Int, min: Int, max: Int): Int = if (v < min) min else if (v > max) max else v
	@JvmStatic fun clamp(value: Double, min: Double, max: Double): Double = if (value < min) min else if (value > max) max else value
	@JvmStatic fun clampSpecial(value: Double, min: Double, max: Double): Double = if (max >= min) clamp(value, min, max) else value

	@JvmStatic fun clamp(value: Float, min: Float, max: Float): Float = if (value < min) min else if (value > max) max else value
	@JvmStatic fun clampInt(value: Int, min: Int, max: Int): Int = if (value < min) min else if (value > max) max else value
	@JvmStatic fun clampf255(v: Double): Int = if (v < 0.0) 0 else if (v > 1.0) 255 else (v * 255).toInt()
	@JvmStatic fun clampf01(v: Double): Double = if (v < 0.0) 0.0 else if (v > 1.0) 1.0 else v
	@JvmStatic fun clampn255(v: Int): Int = if (v < -255) -255 else if (v > 255) 255 else v
	@JvmStatic fun clamp255(v: Int): Int = if (v < 0) 0 else if (v > 255) 255 else v

	@JvmStatic fun distance(a: Double, b: Double): Double = Math.abs(a - b)
	@JvmStatic fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double = Math.hypot(x1 - x2, y1 - y2)
	@JvmStatic fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double = Math.hypot((x1 - x2).toDouble(), (y1 - y2).toDouble())
	@JvmStatic fun distance(a: Vector2, b: Vector2): Double = distance(a.x, a.y, b.x, b.y)
	@JvmStatic fun distance(a: PointInt, b: PointInt): Double = distance(a.x, a.y, b.x, b.y)

	@JvmStatic fun smoothstep(edge0: Double, edge1: Double, step: Double): Double {
		val step2 = clamp((step - edge0) / (edge1 - edge0), 0.0, 1.0)
		return step2 * step2 * (3 - 2 * step2)
	}

	@JvmStatic fun interpolate(v0: Double, v1: Double, step: Double): Double = v0 * (1 - step) + v1 * step

	@JvmStatic fun modUnsigned(num: Double, den: Double): Double {
		var result: Double = (num % den)
		if (result < 0) result += den
		return result
	}

	@JvmStatic fun between(value: Double, min: Double, max: Double): Boolean = (value >= min) && (value <= max)

	@JvmStatic fun convertRange(value: Double, minSrc: Double, maxSrc: Double, minDst: Double, maxDst: Double): Double = (((value - minSrc) / (maxSrc - minSrc)) * (maxDst - minDst)) + minDst

	@JvmStatic fun sign(x: Double): Int = if (x < 0) -1 else if (x > 0) +1 else 0
	@JvmStatic fun signNonZeroM1(x: Double): Int = if (x <= 0) -1 else +1
	@JvmStatic fun signNonZeroP1(x: Double): Int = if (x >= 0) +1 else -1

	@JvmStatic fun multiplyIntegerUnsigned(a: Int, b: Int) = (a * b) or 0
	@JvmStatic fun multiplyIntegerSigned(a: Int, b: Int): Int = (a * b) or 0
	@JvmStatic fun divideIntegerUnsigned(a: Int, b: Int): Int = (a / b) or 0
	@JvmStatic fun divideIntegerSigned(a: Int, b: Int): Int = (a / b) or 0
	@JvmStatic fun hypot(x: Double, y: Double): Double = Math.sqrt(x * x + y * y)
	@JvmStatic fun hypotNoSqrt(x: Double, y: Double): Double = (x * x + y * y)

	@JvmStatic fun roundDecimalPlaces(value: Double, places: Int): Double {
		val placesFactor: Double = Math.pow(10.0, places.toDouble())
		return Math.round(value * placesFactor) / placesFactor
	}

	@JvmStatic fun isEquivalent(a: Double, b: Double, epsilon: Double = 0.0001): Boolean = (a - epsilon < b) && (a + epsilon > b)
	@JvmStatic fun packUintFast(r: Int, g: Int, b: Int, a: Int): Int = (a shl 24) or (b shl 16) or (g shl 8) or (r shl 0)
	@JvmStatic fun pack4fUint(r: Double, g: Double, b: Double, a: Double): Int = packUintFast(clampf255(r), clampf255(g), clampf255(b), clampf255(a))
	@JvmStatic fun log2(v: Int): Int = (Math.log(v.toDouble()) / Math.log(2.0)).toInt()

	@JvmStatic fun distanceXY(x1: Double, y1: Double, x2: Double, y2: Double): Double = hypot(x1 - x2, y1 - y2);
	@JvmStatic fun distancePoint(a: Vector2, b: Vector2): Double = distanceXY(a.x, a.y, b.x, b.y);
}