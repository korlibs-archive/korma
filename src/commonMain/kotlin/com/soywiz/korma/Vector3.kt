package com.soywiz.korma

import com.soywiz.korma.internal.*

interface IVector3 {
    val x: Double
    val y: Double
    val z: Double
}

data class Vector3(override var x: Double, override var y: Double, override var z: Double) : IVector3 {
    override fun toString(): String = "(${x.niceStr}, ${y.niceStr}, ${z.niceStr})"
}

inline fun IVector3(x: Number, y: Number, z: Number): Vector3 = Vector3(x.toDouble(), y.toDouble(), z.toDouble())

inline class IntVector3(val v: Vector3) {
    val x: Int get() = v.x.toInt()
    val y: Int get() = v.y.toInt()
    val z: Int get() = v.z.toInt()
}

fun Vector3.asIntVector3() = IntVector3(this)
