package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
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

    operator fun get(index: Int): Float = data[index]
    operator fun set(index: Int, value: Float) = run { data[index] = value }

    companion object {
        inline operator fun invoke(x: Number, y: Number, z: Number, w: Number = 1f): Vector3D = Vector3D().setTo(x, y, z, w)
    }

    fun copyFrom(other: Vector3D) = setTo(other.x, other.y, other.z, other.w)
    fun setTo(x: Float, y: Float, z: Float, w: Float): Vector3D = this.apply { this.x = x; this.y = y; this.z = z; this.w = w }
    inline fun setTo(x: Number, y: Number, z: Number, w: Number): Vector3D = setTo(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

    fun transform(mat: Matrix3D) = mat.transform(this, this)

    override fun equals(other: Any?): Boolean = (other is Vector3D) && almostEquals(this.x, other.x) && almostEquals(this.y, other.y) && almostEquals(this.z, other.z) && almostEquals(this.w, other.w)
    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = "(${x.niceStr}, ${y.niceStr}, ${z.niceStr}, ${w.niceStr})"
}

inline class IntVector3(val v: Vector3D) {
    val x: Int get() = v.x.toInt()
    val y: Int get() = v.y.toInt()
    val z: Int get() = v.z.toInt()
    val w: Int get() = v.w.toInt()
}

fun Vector3D.asIntVector3D() = IntVector3(this)
