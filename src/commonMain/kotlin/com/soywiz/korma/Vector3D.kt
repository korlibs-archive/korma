package com.soywiz.korma

import com.soywiz.korma.internal.*

interface IVector3D {
    val x: Float
    val y: Float
    val z: Float
    val w: Float
}

data class Vector3D(override var x: Float, override var y: Float, override var z: Float, override var w: Float = 0f) : IVector3D {
    override fun toString(): String = "(${x.niceStr}, ${y.niceStr}, ${z.niceStr}, ${w.niceStr})"
}

inline fun IVector3D(x: Number, y: Number, z: Number, w: Number = 0.0): Vector3D = Vector3D(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

inline class IntVector3D(val v: Vector3D) {
    val x: Int get() = v.x.toInt()
    val y: Int get() = v.y.toInt()
    val z: Int get() = v.z.toInt()
    val w: Int get() = v.w.toInt()
}

fun Vector3D.asIntVector3() = IntVector3D(this)
