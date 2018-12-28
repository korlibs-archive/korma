@file:Suppress("NOTHING_TO_INLINE")

package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*
import kotlin.math.*

interface IPoint {
    val x: Double
    val y: Double

    companion object {
        operator fun invoke(): IPoint = Point(0.0, 0.0)
        inline operator fun invoke(x: Number, y: Number): IPoint = Point(x.toDouble(), y.toDouble())
    }
}

data class Point(override var x: Double, override var y: Double) : MutableInterpolable<Point>, Interpolable<Point>, Comparable<IPoint>, IPoint {
    override fun compareTo(other: IPoint): Int = compare(this.x, this.y, other.x, other.y)

    companion object {
        val Zero: IPoint = Point(0.0, 0.0)
        val One: IPoint = Point(1.0, 1.0)

        val Up: IPoint = Point(0.0, +1.0)
        val Down: IPoint = Point(0.0, -1.0)
        val Left: IPoint = Point(-1.0, 0.0)
        val Right: IPoint = Point(+1.0, 0.0)

        //inline operator fun invoke(): Point = Point(0.0, 0.0) // @TODO: // e: java.lang.NullPointerException at org.jetbrains.kotlin.com.google.gwt.dev.js.JsAstMapper.mapFunction(JsAstMapper.java:562) (val pt = Array(1) { Point() })
        operator fun invoke(): Point = Point(0.0, 0.0)
        operator fun invoke(v: IPoint): Point = Point(v.x, v.y)
        inline operator fun invoke(x: Number, y: Number): Point = Point(x.toDouble(), y.toDouble())
        inline operator fun invoke(xy: Number): Point = Point(xy.toDouble(), xy.toDouble())

        fun middle(a: IPoint, b: IPoint): Point = Point((a.x + b.x) * 0.5, (a.y + b.y) * 0.5)

        fun angle(a: IPoint, b: IPoint): Angle = Angle.fromRadians(acos((a.dot(b)) / (a.length * b.length)))

        fun angle(ax: Double, ay: Double, bx: Double, by: Double): Angle = Angle.between(ax, ay, bx, by)
            //acos(((ax * bx) + (ay * by)) / (hypot(ax, ay) * hypot(bx, by)))

        fun compare(lx: Double, ly: Double, rx: Double, ry: Double): Int {
            val ret = ly.compareTo(ry)
            return if (ret == 0) lx.compareTo(rx) else ret
        }

        fun compare(l: IPoint, r: IPoint): Int = compare(l.x, l.y, r.x, r.y)

        fun angle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Angle = Angle.between(x1 - x2, y1 - y2, x1 - x3, y1 - y3)

        //val ax = x1 - x2
        //val ay = y1 - y2
        //val al = hypot(ax, ay)
        //val bx = x1 - x3
        //val by = y1 - y3
        //val bl = hypot(bx, by)
        //return acos((ax * bx + ay * by) / (al * bl))
    }

    fun setTo(x: Double, y: Double): Point {
        this.x = x
        this.y = y
        return this
    }

    fun setToZero() = setTo(0.0, 0.0)
    fun neg() = setTo(-x, -y)
    fun mul(s: Double) = setTo(x * s, y * s)
    fun add(p: IPoint) = this.setToAdd(this, p)
    fun sub(p: IPoint) = this.setToSub(this, p)

    fun copyFrom(that: IPoint) = setTo(that.x, that.y)

    fun setToTransform(mat: IMatrix, p: IPoint): Point = setToTransform(mat, p.x, p.y)

    fun setToTransform(mat: IMatrix, x: Double, y: Double): Point = setTo(
        mat.transformX(x, y),
        mat.transformY(x, y)
    )

    fun setToAdd(a: IPoint, b: IPoint): Point = setTo(a.x + b.x, a.y + b.y)
    fun setToSub(a: IPoint, b: IPoint): Point = setTo(a.x - b.x, a.y - b.y)

    fun setToMul(a: IPoint, b: IPoint): Point = setTo(a.x * b.x, a.y * b.y)
    fun setToMul(a: IPoint, s: Double): Point = setTo(a.x * s, a.y * s)
    inline fun setToMul(a: IPoint, s: Number): Point = setToMul(a, s.toDouble())

    fun setToDiv(a: IPoint, b: IPoint): Point = setTo(a.x / b.x, a.y / b.y)
    fun setToDiv(a: IPoint, s: Double): Point = setTo(a.x / s, a.y / s)
    inline fun setToDiv(a: IPoint, s: Number): Point = setToDiv(a, s.toDouble())

    operator fun plusAssign(that: IPoint) {
        setTo(this.x + that.x, this.y + that.y)
    }

    fun normalize() {
        val len = this.length
        this.setTo(this.x / len, this.y / len)
    }

    override fun interpolateWith(ratio: Double, other: Point): Point =
        Point().setToInterpolated(ratio, this, other)

    override fun setToInterpolated(ratio: Double, l: Point, r: Point): Point =
        this.setTo(ratio.interpolate(l.x, r.x), ratio.interpolate(l.y, r.y))

    override fun toString(): String = "(${x.niceStr}, ${y.niceStr})"
}

inline fun Point.mul(s: Number) = mul(s.toDouble())

val Point.unit: IPoint get() = this / length

inline fun Point.setTo(x: Number, y: Number): Point = setTo(x.toDouble(), y.toDouble())

// @TODO: mul instead of dot
operator fun IPoint.plus(that: IPoint): IPoint = IPoint(x + that.x, y + that.y)
operator fun IPoint.minus(that: IPoint): IPoint = IPoint(x - that.x, y - that.y)
operator fun IPoint.times(that: IPoint): IPoint = IPoint(x * that.x, y * that.y)
operator fun IPoint.div(that: IPoint): IPoint = IPoint(x / that.x, y / that.y)

inline operator fun IPoint.times(scale: Number): IPoint = IPoint(x * scale.toDouble(), y * scale.toDouble())
inline operator fun IPoint.div(scale: Number): IPoint = IPoint(x / scale.toDouble(), y / scale.toDouble())

infix fun IPoint.dot(that: IPoint): Double = this.x * that.x + this.y * that.y
inline fun IPoint.distanceTo(x: Number, y: Number): Double = hypot(x.toDouble() - this.x, y.toDouble() - this.y)
fun IPoint.distanceTo(that: IPoint): Double = distanceTo(that.x, that.y)

fun IPoint.angleTo(other: IPoint): Angle = Angle.between(this.x, this.y, other.x, other.y)

fun IPoint.transformed(mat: IMatrix, out: Point = Point()): Point = out.setToTransform(mat, this)

operator fun IPoint.get(index: Int) = when (index) {
    0 -> x; 1 -> y
    else -> throw IndexOutOfBoundsException("IPoint doesn't have $index component")
}

val IPoint.unit: IPoint get() = this / this.length
val IPoint.length: Double get() = hypot(x, y)
val IPoint.magnitude: Double get() = hypot(x, y)
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

inline class PointInt(val p: Point) : IPointInt, Comparable<IPointInt> {
    override fun compareTo(other: IPointInt): Int = compare(this.x, this.y, other.x, other.y)

    companion object {
        operator fun invoke(): PointInt = PointInt(0, 0)
        operator fun invoke(x: Int, y: Int): PointInt = PointInt(Point(x, y))

        fun compare(lx: Int, ly: Int, rx: Int, ry: Int): Int {
            val ret = ly.compareTo(ry)
            return if (ret == 0) lx.compareTo(rx) else ret
        }
    }
    override var x: Int
        set(value) = run { p.x = value.toDouble() }
        get() = p.x.toInt()
    override var y: Int
        set(value) = run { p.y = value.toDouble() }
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
fun PointInt.asDouble(): Point = this.p

val IPoint.int get() = PointInt(x.toInt(), y.toInt())
val IPointInt.float get() = IPoint(x.toDouble(), y.toDouble())

fun Iterable<IPoint>.getPolylineLength(): Double {
    var out = 0.0
    var prev: IPoint? = null
    for (cur in this) {
        if (prev != null) out += prev.distanceTo(cur)
        prev = cur
    }
    return out
}

fun Iterable<IPoint>.bounds(out: Rectangle = Rectangle(), bb: BoundsBuilder = BoundsBuilder()): Rectangle = BoundsBuilder().add(this).getBounds(out)
