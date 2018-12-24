package com.soywiz.korma

import com.soywiz.korma.algo.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*
import kotlin.math.*

interface Vector2 {
    val x: Float
    val y: Float

    companion object {
        val Zero = Vector2(0, 0)
        val One = Vector2(1, 1)

        val Up = Vector2(0, +1)
        val Down = Vector2(0, -1)
        val Left = Vector2(-1, 0)
        val Right = Vector2(+1, 0)

        operator fun invoke(v: Vector2): Vector2 = Vector2(v.x, v.y)
        inline operator fun invoke(): Vector2 = IVector2(0f, 0f)
        inline operator fun invoke(xy: Number): Vector2 = IVector2(xy.toFloat(), xy.toFloat())
        inline operator fun invoke(x: Number, y: Number): Vector2 = IVector2(x.toFloat(), y.toFloat())

        fun middle(a: Vector2, b: Vector2): MVector2 = MVector2((a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f)

        fun angle(a: Vector2, b: Vector2): Float = acos((a.dot(b)) / (a.length * b.length))

        fun angle(ax: Float, ay: Float, bx: Float, by: Float): Float =
            acos(((ax * bx) + (ay * by)) / (hypot(ax, ay) * hypot(bx, by)))

        object Vector2Sorter : SortOps<PointArrayList>() {
            override fun PointArrayList.compare(l: Int, r: Int): Int = compare(getX(l), getY(l), getX(r), getY(r))
            override fun PointArrayList.swapIndices(l: Int, r: Int) = this.swap(l, r)
        }

        fun compare(lx: Float, ly: Float, rx: Float, ry: Float): Int {
            val ret = ly.compareTo(ry)
            return if (ret == 0) lx.compareTo(rx) else ret
        }

        fun compare(l: Vector2, r: Vector2): Int = compare(l.x, l.y, r.x, r.y)

        fun angle(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): Float {
            val ax = x1 - x2
            val ay = y1 - y2
            val al = hypot(ax, ay)

            val bx = x1 - x3
            val by = y1 - y3
            val bl = hypot(bx, by)

            return acos((ax * bx + ay * by) / (al * bl))
        }
    }

    abstract class Base : Vector2 {
        override fun equals(other: Any?): Boolean =
            if (other is Vector2) this.x == other.x && this.y == other.y else false

        override fun hashCode(): Int = x.hashCode() + (y.hashCode() shl 7)
        override fun toString(): String = "(${x.niceStr}, ${y.niceStr})"
    }
}


@PublishedApi
internal class IVector2(override val x: Float, override val y: Float) : Vector2.Base(), Interpolable<Vector2> {
    override fun interpolateWith(other: Vector2, ratio: Double): Vector2 {
        return Vector2(
            ratio.interpolate(this.x, other.x),
            ratio.interpolate(this.y, other.y)
        )
    }
}

class MVector2(override var x: Float = 0f, override var y: Float = x) : MutableInterpolable<MVector2>, Interpolable<MVector2>, Vector2.Base() {

    companion object {
        inline operator fun invoke(v: Vector2) = MVector2(v.x, v.y)
    	inline operator fun invoke(x: Number, y: Number) = MVector2(x.toFloat(), y.toFloat())
    }

    inline fun setTo(x: Number, y: Number): MVector2 = setTo(x.toFloat(), y.toFloat())

    fun setTo(x: Float, y: Float): MVector2 {
        this.x = x
        this.y = y
        return this
    }

    fun setToZero() = setTo(0f, 0f)

    /// Negate this point.
    fun neg() = setTo(-x, -y)

    fun mul(s: Float) = setTo(x * s, y * s)
    inline fun mul(s: Number) = mul(s.toFloat())
    fun add(p: Vector2) = this.setToAdd(this, p)
    fun sub(p: Vector2) = this.setToSub(this, p)

    fun copyFrom(that: Vector2) = setTo(that.x, that.y)

    fun setToTransform(mat: IMatrix, p: Vector2): MVector2 = setToTransform(mat, p.x.toFloat(), p.y.toFloat())

    fun setToTransform(mat: IMatrix, x: Float, y: Float): MVector2 = setTo(
        mat.transformX(x, y),
        mat.transformY(x, y)
    )

    fun setToAdd(a: Vector2, b: Vector2): MVector2 = setTo(a.x + b.x, a.y + b.y)
    fun setToSub(a: Vector2, b: Vector2): MVector2 = setTo(a.x - b.x, a.y - b.y)

    fun setToMul(a: Vector2, b: Vector2): MVector2 = setTo(a.x * b.x, a.y * b.y)
    fun setToMul(a: Vector2, s: Float): MVector2 = setTo(a.x * s, a.y * s)

    fun setToDiv(a: Vector2, b: Vector2): MVector2 = setTo(a.x / b.x, a.y / b.y)
    fun setToDiv(a: Vector2, s: Float): MVector2 = setTo(a.x / s, a.y / s)

    operator fun plusAssign(that: Vector2) {
        setTo(this.x + that.x, this.y + that.y)
    }

    fun normalize() {
        val len = this.length
        this.setTo(this.x / len, this.y / len)
    }

    val length: Float get() = hypot(x, y)

    fun distanceTo(x: Float, y: Float) = hypot(x - this.x, y - this.y)
    fun distanceTo(that: Vector2) = distanceTo(that.x, that.y)

    override fun interpolateWith(other: MVector2, ratio: Double): MVector2 =
        MVector2().setToInterpolated(this, other, ratio)

    override fun setToInterpolated(l: MVector2, r: MVector2, ratio: Double): MVector2 =
        this.setTo(ratio.interpolate(l.x, r.x), ratio.interpolate(l.y, r.y))
}

@Deprecated("", ReplaceWith("vec(x, y)"))
inline fun Vec(x: Number, y: Number): Vector2 = vec(x, y)
inline fun vec(x: Number, y: Number): Vector2 = MVector2(x.toFloat(), y.toFloat())

val MVector2.unit: Vector2 get() = this / length
/*
operator fun MVector2.plus(that: Vector2) = MVector2(this.x + that.x, this.y + that.y)
operator fun MVector2.minus(that: Vector2) = MVector2(this.x - that.x, this.y - that.y)
operator fun MVector2.times(that: Vector2) = this.x * that.x + this.y * that.y
operator fun MVector2.times(v: Float) = MVector2(x * v, y * v)
operator fun MVector2.div(v: Float) = MVector2(x / v, y / v)
*/

// @TODO: mul instead of dot
operator fun Vector2.plus(that: Vector2): Vector2 = Vector2(this.x + that.x, this.y + that.y)

operator fun Vector2.minus(that: Vector2): Vector2 = Vector2(this.x - that.x, this.y - that.y)
operator fun Vector2.times(that: Vector2): Vector2 = Vector2(this.x * that.x, this.y * that.y)
operator fun Vector2.div(that: Vector2): Vector2 = Vector2(this.x / that.x, this.y / that.y)

operator fun Vector2.times(scale: Float): Vector2 = Vector2(this.x * scale, this.y * scale)
operator fun Vector2.div(scale: Float): Vector2 = Vector2(this.x / scale, this.y / scale)

inline operator fun Vector2.times(scale: Number): Vector2 = Vector2(this.x * scale.toFloat(), this.y * scale.toFloat())
inline operator fun Vector2.div(scale: Number): Vector2 = Vector2(this.x / scale.toFloat(), this.y / scale.toFloat())

infix fun Vector2.dot(that: Vector2) = this.x * that.x + this.y * that.y
//infix fun Vector2.mul(that: Vector2) = Vector2(this.x * that.x, this.y * that.y)
fun Vector2.distanceTo(x: Float, y: Float) = hypot(x - this.x, y - this.y)
inline fun Vector2.distanceTo(x: Number, y: Number) = distanceTo(x.toFloat(), y.toFloat())

fun Vector2.distanceTo(that: Vector2) = distanceTo(that.x, that.y)

fun Vector2.angleToRad(other: Vector2): Float = Angle.betweenRad(this.x.toDouble(), this.y.toDouble(), other.x.toDouble(), other.y.toDouble()).toFloat()
fun Vector2.angleTo(other: Vector2): Angle = Angle.between(this.x.toDouble(), this.y.toDouble(), other.x.toDouble(), other.y.toDouble())

fun Vector2.transformed(mat: IMatrix, out: MVector2 = MVector2()): MVector2 = out.setToTransform(mat, this)

operator fun Vector2.get(index: Int) = when (index) {
    0 -> x; 1 -> y
    else -> throw IndexOutOfBoundsException("Vector2 doesn't have $index component")
}

val Vector2.unit: Vector2 get() = this / this.length
val Vector2.length: Float get() = hypot(x, y)
val Vector2.magnitude: Float get() = hypot(x, y)
val Vector2.normalized: Vector2
    get() {
        val imag = 1f / magnitude
        return Vector2(x * imag, y * imag)
    }

val Vector2.mutable: MVector2 get() = MVector2(x, y)
val Vector2.immutable: Vector2 get() = Vector2(x, y)
fun Vector2.copy() = Vector2(x, y)

interface Vector2Int {
    val x: Int
    val y: Int

    companion object {
        operator fun invoke(x: Int, y: Int): Vector2Int = IVector2Int(x, y)
    }
}

internal data class IVector2Int(override val x: Int, override val y: Int) : Vector2Int {
    override fun toString(): String = "($x, $y)"
}

data class MVector2Int(override var x: Int = 0, override var y: Int = 0) : Vector2Int {
    fun setTo(x: Int, y: Int) = this.apply { this.x = x; this.y = y }
    fun setTo(that: Vector2Int) = this.setTo(that.x, that.y)
    override fun toString(): String = "($x, $y)"
}

operator fun Vector2Int.plus(that: Vector2Int) = Vector2Int(this.x + that.x, this.y + that.y)
operator fun Vector2Int.minus(that: Vector2Int) = Vector2Int(this.x - that.x, this.y - that.y)
operator fun Vector2Int.times(that: Vector2Int) = Vector2Int(this.x * that.x, this.y * that.y)
operator fun Vector2Int.div(that: Vector2Int) = Vector2Int(this.x / that.x, this.y / that.y)
operator fun Vector2Int.rem(that: Vector2Int) = Vector2Int(this.x % that.x, this.y % that.y)

val Vector2Int.mutable: MVector2Int get() = MVector2Int(x, y)
val Vector2Int.immutable: Vector2Int get() = Vector2Int(x, y)

val Vector2.int get() = Vector2Int(x.toInt(), y.toInt())
val Vector2Int.double get() = Vector2(x.toFloat(), y.toFloat())

@Suppress("NOTHING_TO_INLINE")
class MVector2Area(val size: Int) {
    @PublishedApi
    internal val points = Array(size) { MPoint2d() }
    @PublishedApi
    internal var offset = 0

    @PublishedApi
    internal fun alloc() = points[offset++]

    operator fun Vector2.plus(other: Vector2): Vector2 = alloc().setToAdd(this, other)
    operator fun Vector2.minus(other: Vector2): Vector2 = alloc().setToSub(this, other)

    operator fun Vector2.times(value: Vector2): Vector2 = alloc().setToMul(this, value)
    inline operator fun Vector2.times(value: Number): Vector2 = alloc().setToMul(this, value.toFloat())

    operator fun Vector2.div(value: Vector2): Vector2 = alloc().setToDiv(this, value)
    inline operator fun Vector2.div(value: Number): Vector2 = alloc().setToDiv(this, value.toFloat())

    inline operator fun invoke(callback: MVector2Area.() -> Unit) {
        val oldOffset = offset
        try {
            callback()
        } finally {
            offset = oldOffset
        }
    }
}
