@file:Suppress("NOTHING_TO_INLINE")

package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*
import kotlin.math.*

interface IPoint {
    val x: Float
    val y: Float

    companion object {
        inline operator fun invoke(x: Number, y: Number): IPoint = Point(x.toFloat(), y.toFloat())
    }
}

data class Point(override var x: Float, override var y: Float) : MutableInterpolable<Point>, Interpolable<Point>, IPoint {
    companion object {
        val Zero: IPoint = Point(0f, 0f)
        val One: IPoint = Point(1f, 1f)

        val Up: IPoint = Point(0f, +1f)
        val Down: IPoint = Point(0f, -1f)
        val Left: IPoint = Point(-1f, 0f)
        val Right: IPoint = Point(+1f, 0f)

        inline operator fun invoke(x: Number, y: Number): Point = Point(x.toFloat(), y.toFloat())
        inline operator fun invoke(xy: Number): Point = Point(xy.toFloat(), xy.toFloat())
        inline operator fun invoke(v: IPoint): Point = Point(v.x, v.y)
        inline operator fun invoke(): Point = Point(0.0, 0.0)

        fun middle(a: IPoint, b: IPoint): Point = Point((a.x + b.x) * 0.5, (a.y + b.y) * 0.5)

        fun angle(a: IPoint, b: IPoint): Angle = Angle.fromRadians(acos((a.dot(b)) / (a.length * b.length)))

        fun angle(ax: Float, ay: Float, bx: Float, by: Float): Angle = Angle.between(ax, ay, bx, by)
            //acos(((ax * bx) + (ay * by)) / (hypot(ax, ay) * hypot(bx, by)))

        fun compare(lx: Float, ly: Float, rx: Float, ry: Float): Int {
            val ret = ly.compareTo(ry)
            return if (ret == 0) lx.compareTo(rx) else ret
        }

        fun compare(l: IPoint, r: IPoint): Int = compare(l.x, l.y, r.x, r.y)

        fun angle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Angle = Angle.between(x1 - x2, y1 - y2, x1 - x3, y1 - y3)

        //val ax = x1 - x2
        //val ay = y1 - y2
        //val al = hypot(ax, ay)
        //val bx = x1 - x3
        //val by = y1 - y3
        //val bl = hypot(bx, by)
        //return acos((ax * bx + ay * by) / (al * bl))
    }

    fun setTo(x: Float, y: Float): Point {
        this.x = x
        this.y = y
        return this
    }

    fun setToZero() = setTo(0.0, 0.0)
    fun neg() = setTo(-x, -y)
    fun mul(s: Float) = setTo(x * s, y * s)
    fun add(p: IPoint) = this.setToAdd(this, p)
    fun sub(p: IPoint) = this.setToSub(this, p)

    fun copyFrom(that: IPoint) = setTo(that.x, that.y)

    fun setToTransform(mat: IMatrix, p: IPoint): Point = setToTransform(mat, p.x, p.y)

    fun setToTransform(mat: IMatrix, x: Float, y: Float): Point = setTo(
        mat.transformX(x, y),
        mat.transformY(x, y)
    )

    fun setToAdd(a: IPoint, b: IPoint): Point = setTo(a.x + b.x, a.y + b.y)
    fun setToSub(a: IPoint, b: IPoint): Point = setTo(a.x - b.x, a.y - b.y)

    fun setToMul(a: IPoint, b: IPoint): Point = setTo(a.x * b.x, a.y * b.y)
    fun setToMul(a: IPoint, s: Double): Point = setTo(a.x * s, a.y * s)

    fun setToDiv(a: IPoint, b: IPoint): Point = setTo(a.x / b.x, a.y / b.y)
    fun setToDiv(a: IPoint, s: Double): Point = setTo(a.x / s, a.y / s)

    operator fun plusAssign(that: IPoint) {
        setTo(this.x + that.x, this.y + that.y)
    }

    fun normalize() {
        val len = this.length
        this.setTo(this.x / len, this.y / len)
    }

    override fun interpolateWith(other: Point, ratio: Float): Point =
        Point().setToInterpolated(this, other, ratio)

    override fun setToInterpolated(l: Point, r: Point, ratio: Float): Point =
        this.setTo(ratio.interpolate(l.x, r.x), ratio.interpolate(l.y, r.y))

    override fun toString(): String = "(${x.niceStr}, ${y.niceStr})"
}

inline fun Point.mul(s: Number) = mul(s.toFloat())

val Point.unit: IPoint get() = this / length

inline fun Point.setTo(x: Number, y: Number): Point = setTo(x.toFloat(), y.toFloat())

// @TODO: mul instead of dot
operator fun IPoint.plus(that: IPoint): IPoint = IPoint(x + that.x, y + that.y)
operator fun IPoint.minus(that: IPoint): IPoint = IPoint(x - that.x, y - that.y)
operator fun IPoint.times(that: IPoint): IPoint = IPoint(x * that.x, y * that.y)
operator fun IPoint.div(that: IPoint): IPoint = IPoint(x / that.x, y / that.y)

inline operator fun IPoint.times(scale: Number): IPoint = IPoint(x * scale.toFloat(), y * scale.toFloat())
inline operator fun IPoint.div(scale: Number): IPoint = IPoint(x / scale.toFloat(), y / scale.toFloat())

infix fun IPoint.dot(that: IPoint): Float = this.x * that.x + this.y * that.y
inline fun IPoint.distanceTo(x: Number, y: Number): Float = hypot(x.toFloat() - this.x, y.toFloat() - this.y)
fun IPoint.distanceTo(that: IPoint) = distanceTo(that.x, that.y)

fun IPoint.angleToRad(other: IPoint): Float = Angle.betweenRad(this.x, this.y, other.x, other.y)
fun IPoint.angleTo(other: IPoint): Angle = Angle.between(this.x, this.y, other.x, other.y)

fun IPoint.transformed(mat: IMatrix, out: Point = Point()): Point = out.setToTransform(mat, this)

operator fun IPoint.get(index: Int) = when (index) {
    0 -> x; 1 -> y
    else -> throw IndexOutOfBoundsException("IPoint doesn't have $index component")
}

val IPoint.unit: IPoint get() = this / this.length
val IPoint.length: Float get() = hypot(x, y)
val IPoint.magnitude: Float get() = hypot(x, y)
val IPoint.normalized: IPoint
    get() {
        val imag = 1.0 / magnitude
        return IPoint(x * imag, y * imag)
    }

val IPoint.mutable: Point get() = Point(x, y)
val IPoint.immutable: IPoint get() = IPoint(x, y)
fun IPoint.copy() = IPoint(x, y)

interface IPointInt {
    val x: Int
    val y: Int

    companion object {
        operator fun invoke(x: Int, y: Int): IPointInt = PointInt(x, y)
    }
}

inline class PointInt(val p: Point) : IPointInt {
    companion object {
        operator fun invoke(): PointInt = PointInt(0, 0)
        operator fun invoke(x: Int, y: Int): PointInt = PointInt(Point(x, y))
    }
    override var x: Int
        set(value) = run { p.x = value.toFloat() }
        get() = p.x.toInt()
    override var y: Int
        set(value) = run { p.y = value.toFloat() }
        get() = p.y.toInt()
    fun setTo(x: Int, y: Int) = this.apply { this.x = x; this.y = y }
    fun setTo(that: IPointInt) = this.setTo(that.x, that.y)
    override fun toString(): String = "($x, $y)"
}

operator fun IPointInt.plus(that: IPointInt) = PointInt(this.x + that.x, this.y + that.y)
operator fun IPointInt.minus(that: IPointInt) = PointInt(this.x - that.x, this.y - that.y)
operator fun IPointInt.times(that: IPointInt) = PointInt(this.x * that.x, this.y * that.y)
operator fun IPointInt.div(that: IPointInt) = PointInt(this.x / that.x, this.y / that.y)
operator fun IPointInt.rem(that: IPointInt) = PointInt(this.x % that.x, this.y % that.y)

fun Point.asInt(): PointInt = PointInt(this)
fun PointInt.asFloat(): Point = this.p

val IPoint.int get() = PointInt(x.toInt(), y.toInt())
val IPointInt.float get() = IPoint(x.toFloat(), y.toFloat())

fun Iterable<IPoint>.getPolylineLength(): Float {
    var out = 0f
    var prev: IPoint? = null
    for (cur in this) {
        if (prev != null) out += prev.distanceTo(cur)
        prev = cur
    }
    return out
}

fun Iterable<IPoint>.bounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(
    left = this.map { it.x }.min() ?: 0f,
    top = this.map { it.y }.min() ?: 0f,
    right = this.map { it.x }.max() ?: 0f,
    bottom = this.map { it.y }.max() ?: 0f
)
