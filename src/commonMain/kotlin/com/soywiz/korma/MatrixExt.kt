package com.soywiz.korma

fun Matrix2d.toMatrix4(out: Matrix4 = Matrix4()): Matrix4 = out.setTo(
    a.toFloat(), b.toFloat(), tx.toFloat(), 0f,
    c.toFloat(), d.toFloat(), ty.toFloat(), 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun Matrix4.copyFrom(that: Matrix2d): Matrix4 {
    return setTo(
        that.a.toFloat(), that.b.toFloat(), 0f, 0f,
        that.c.toFloat(), that.d.toFloat(), 0f, 0f,
        0f, 0f, 1f, 0f,
        that.tx.toFloat(), that.ty.toFloat(), 0f, 1f
    )
}

