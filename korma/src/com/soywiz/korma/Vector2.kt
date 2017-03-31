package com.soywiz.korma

import com.soywiz.korio.util.Extra
import com.soywiz.korio.util.niceStr
import com.soywiz.korma.interpolation.Interpolable
import com.soywiz.korma.interpolation.MutableInterpolable
import com.soywiz.korma.interpolation.interpolate
import java.util.*

data class Vector2(var x: Double = 0.0, var y: Double = x) : MutableInterpolable<Vector2>, Interpolable<Vector2> {
	constructor(x: Float, y: Float) : this(x.toDouble(), y.toDouble())
	constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
	constructor(x: Long, y: Long) : this(x.toDouble(), y.toDouble())
	constructor(v: Vector2) : this(v.x, v.y)

	fun setTo(x: Int, y: Int): Vector2 = setTo(x.toDouble(), y.toDouble())

	fun setTo(x: Double, y: Double): Vector2 {
		this.x = x
		this.y = y
		return this
	}

	fun setToZero() = setTo(0.0, 0.0)

	/// Negate this point.
	fun neg() = setTo(-x, -y)
	fun mul(s: Double) = setTo(x * s, y * s)
	fun add(p: Vector2) = this.setToAdd(this, p)
	fun sub(p: Vector2) = this.setToSub(this, p)

	fun copyFrom(that: Vector2) = setTo(that.x, that.y)

	fun setToTransform(mat: Matrix2d, p: Vector2): Vector2 = setToTransform(mat, p.x, p.y)

	fun setToTransform(mat: Matrix2d, x: Double, y: Double): Vector2 = setTo(
		mat.transformX(x, y),
		mat.transformY(x, y)
	)

	fun setToAdd(a: Vector2, b: Vector2): Vector2 = setTo(a.x + b.x, a.y + b.y)
	fun setToSub(a: Vector2, b: Vector2): Vector2 = setTo(a.x - b.x, a.y - b.y)

	operator fun plusAssign(that: Vector2) {
		setTo(this.x + that.x, this.y + that.y)
	}

	fun normalize() {
		val len = this.length
		this.setTo(this.x / len, this.y / len)
	}

	val unit: Vector2 get() = this / length
	val length: Double get() = Math.hypot(x, y)
	operator fun plus(that: Vector2) = Vector2(this.x + that.x, this.y + that.y)
	operator fun minus(that: Vector2) = Vector2(this.x - that.x, this.y - that.y)
	operator fun times(that: Vector2) = this.x * that.x + this.y * that.y
	operator fun times(v: Double) = Vector2(x * v, y * v)
	operator fun div(v: Double) = Vector2(x / v, y / v)

	fun distanceTo(x: Double, y: Double) = Math.hypot(x - this.x, y - this.y)
	fun distanceTo(that: Vector2) = distanceTo(that.x, that.y)

	override fun toString(): String = "Vector2(${x.niceStr}, ${y.niceStr})"

	override fun interpolateWith(other: Vector2, ratio: Double): Vector2 = Vector2().setToInterpolated(this, other, ratio)
	override fun setToInterpolated(l: Vector2, r: Vector2, ratio: Double): Vector2 = this.setTo(ratio.interpolate(l.x, r.x), ratio.interpolate(l.y, r.y))

	companion object {
		fun middle(a: Vector2, b: Vector2): Vector2 = Vector2((a.x + b.x) * 0.5, (a.y + b.y) * 0.5)

		fun angle(a: Vector2, b: Vector2): Double = Math.acos((a * b) / (a.length * b.length))

		fun angle(ax: Double, ay: Double, bx: Double, by: Double): Double = Math.acos(((ax * bx) + (ay * by)) / (Math.hypot(ax, ay) * Math.hypot(bx, by)))

		fun sortPoints(points: ArrayList<Vector2>): Unit {
			points.sortWith(java.util.Comparator({ l, r -> cmpPoints(l, r) }))
		}

		protected fun cmpPoints(l: Vector2, r: Vector2): Int {
			var ret: Double = l.y - r.y
			if (ret == 0.0) ret = l.x - r.x
			if (ret < 0) return -1
			if (ret > 0) return +1
			return 0
		}

		fun angle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Double {
			val ax = x1 - x2
			val ay = y1 - y2
			val al = Math.hypot(ax, ay)

			val bx = x1 - x3
			val by = y1 - y3
			val bl = Math.hypot(bx, by)

			return Math.acos((ax * bx + ay * by) / (al * bl))
		}
	}
}