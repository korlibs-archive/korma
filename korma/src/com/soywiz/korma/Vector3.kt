package com.soywiz.korma

data class Vector3(var x: Double, var y: Double, var z: Double) {

}

inline fun Vector3(x: Number, y: Number, z: Number) = Vector3(x.toDouble(), y.toDouble(), z.toDouble())