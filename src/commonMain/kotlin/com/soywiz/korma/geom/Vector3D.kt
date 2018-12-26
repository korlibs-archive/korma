package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import kotlin.math.*

interface IVector3D {
    val x: Double
    val y: Double
    val z: Double
    val w: Double

    companion object {
        inline operator fun invoke(x: Number, y: Number, z: Number, w: Number): Vector3D = Vector3D(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
    }
}

val IVector3D.length: Double get() = sqrt((x * x) + (y * y) + (z * z) + (w * w))

data class Vector3D(override var x: Double, override var y: Double, override var z: Double, override var w: Double) : IVector3D {
    companion object {
        operator fun invoke(): Vector3D = Vector3D(0.0, 0.0, 0.0, 1.0)
        inline operator fun invoke(x: Number, y: Number, z: Number, w: Number = 1.0): Vector3D = Vector3D(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
    }
    fun setTo(x: Double, y: Double, z: Double, w: Double): Vector3D {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
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
