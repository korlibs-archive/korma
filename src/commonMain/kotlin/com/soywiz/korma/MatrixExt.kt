package com.soywiz.korma

fun Matrix.toMatrix4(out: Matrix4 = Matrix4()): Matrix4 = out.setTo(
    a.toFloat(), b.toFloat(), tx.toFloat(), 0f,
    c.toFloat(), d.toFloat(), ty.toFloat(), 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun Matrix4.copyFrom(that: Matrix): Matrix4 = that.toMatrix4(this)
