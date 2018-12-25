package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import kotlin.math.*

interface IVector3D {
    val x: Float
    val y: Float
    val z: Float
    val w: Float
}

inline fun IVector3D(x: Number, y: Number, z: Number, w: Number): Vector3D = Vector3D(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

val IVector3D.length: Float get() = sqrt((x * x) + (y * y) + (z * z) + (w * w))

data class Vector3D(override var x: Float, override var y: Float, override var z: Float, override var w: Float) : IVector3D {
    companion object {
        inline operator fun invoke(x: Number, y: Number, z: Number, w: Number = 0f): Vector3D = Vector3D(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }
    override fun toString(): String = "(${x.niceStr}, ${y.niceStr}, ${z.niceStr}, ${w.niceStr})"
}

inline class IntVector3(val v: Vector3D) {
    val x: Int get() = v.x.toInt()
    val y: Int get() = v.y.toInt()
    val z: Int get() = v.z.toInt()
    val w: Int get() = v.w.toInt()
}

fun Vector3D.asIntVector3D() = IntVector3(this)
