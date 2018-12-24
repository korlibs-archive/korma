package com.soywiz.korma

fun Matrix.toMatrix4(out: Matrix3D = Matrix3D()): Matrix3D = out.setTo(
    a.toFloat(), b.toFloat(), tx.toFloat(), 0f,
    c.toFloat(), d.toFloat(), ty.toFloat(), 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun Matrix3D.copyFrom(that: Matrix): Matrix3D = that.toMatrix4(this)
