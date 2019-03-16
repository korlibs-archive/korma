package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.interpolate
import com.soywiz.korma.math.*
import kotlin.math.*

class Vector3D {
    val data = floatArrayOf(0f, 0f, 0f, 1f)

    var x: Float get() = data[0]; set(value) = run { data[0] = value }
    var y: Float get() = data[1]; set(value) = run { data[1] = value }
    var z: Float get() = data[2]; set(value) = run { data[2] = value }
    var w: Float get() = data[3]; set(value) = run { data[3] = value }

    val lengthSquared: Float get() = (x * x) + (y * y) + (z * z) + (w * w)
    val length: Float get() = sqrt(lengthSquared)

    val length3Squared: Float get() = (x * x) + (y * y) + (z * z)
    val length3: Float get() = sqrt(length3Squared)

    operator fun get(index: Int): Float = data[index]
    operator fun set(index: Int, value: Float) = run { data[index] = value }

    companion object {
        inline operator fun invoke(x: Number, y: Number, z: Number, w: Number = 1f): Vector3D = Vector3D().setTo(x, y, z, w)

        fun length(x: Double, y: Double, z: Double, w: Double) = sqrt(lengthSq(x, y, z, w))
        inline fun length(x: Number, y: Number, z: Number, w: Number) = length(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

        fun lengthSq(x: Double, y: Double, z: Double, w: Double) = x * x + y * y + z * z + w * w
        inline fun lengthSq(x: Number, y: Number, z: Number, w: Number) = lengthSq(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

        fun length(x: Double, y: Double, z: Double) = sqrt(lengthSq(x, y, z))
        inline fun length(x: Number, y: Number, z: Number) = length(x.toDouble(), y.toDouble(), z.toDouble())

        fun lengthSq(x: Double, y: Double, z: Double) = x * x + y * y + z * z
        inline fun lengthSq(x: Number, y: Number, z: Number) = lengthSq(x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun copyFrom(other: Vector3D) = setTo(other.x, other.y, other.z, other.w)
    fun setTo(x: Float, y: Float, z: Float, w: Float): Vector3D = this.apply { this.x = x; this.y = y; this.z = z; this.w = w }
    inline fun setTo(x: Number, y: Number, z: Number, w: Number): Vector3D = setTo(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    inline fun setToFunc(func: (index: Int) -> Float): Vector3D = setTo(func(0), func(1), func(2), func(3))

    fun transform(mat: Matrix3D) = mat.transform(this, this)

    fun normalize(vector: Vector3D = this): Vector3D = this.apply {
        val norm = 1.0 / vector.length3
        setTo(vector.x * norm, vector.y * norm, vector.z * norm, 1)
    }

    fun normalized(out: Vector3D = Vector3D()): Vector3D = out.copyFrom(this).normalize()

    override fun equals(other: Any?): Boolean = (other is Vector3D) && almostEquals(this.x, other.x) && almostEquals(this.y, other.y) && almostEquals(this.z, other.z) && almostEquals(this.w, other.w)
    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = if (w == 1f) "(${x.niceStr}, ${y.niceStr}, ${z.niceStr})" else "(${x.niceStr}, ${y.niceStr}, ${z.niceStr}, ${w.niceStr})"
}

inline class IntVector3(val v: Vector3D) {
    val x: Int get() = v.x.toInt()
    val y: Int get() = v.y.toInt()
    val z: Int get() = v.z.toInt()
    val w: Int get() = v.w.toInt()
}

fun Vector3D.asIntVector3D() = IntVector3(this)
fun Vector3D.setToInterpolated(left: Vector3D, right: Vector3D, t: Double): Vector3D = setToFunc { t.interpolate(left[it], right[it]) }

fun Vector3D.scale(scale: Float) = this.setTo(this.x * scale, this.y * scale, this.z * scale, this.w * scale)
inline fun Vector3D.scale(scale: Number) = scale(scale.toFloat())

inline fun Vector3D.setTo(x: Number, y: Number, z: Number) = setTo(x, y, z, 1f)

typealias Position3D = Vector3D
typealias Scale3D = Vector3D
