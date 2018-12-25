package com.soywiz.korma

fun Matrix.toMatrix3D(out: Matrix3D = Matrix3D()): Matrix3D = out.copyFrom(this)

fun Matrix3D.copyFrom(that: Matrix): Matrix3D = setTo(
    that.a, that.b, that.tx, 0f,
    that.c, that.d, that.ty, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)
