package com.soywiz.korma

fun Matrix2.toMatrix4(out: Matrix4 = Matrix4()): Matrix4 = out.setTo(
    a, b, 0f, 0f,
    c, d, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun Matrix2d.toMatrix4(out: Matrix4 = Matrix4()): Matrix4 = out.setTo(
    a.toFloat(), b.toFloat(), tx.toFloat(), 0f,
    c.toFloat(), d.toFloat(), ty.toFloat(), 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

fun Matrix3.toMatrix4(out: Matrix4 = Matrix4()): Matrix4 = out.setTo(
    data[0], data[1], data[2], 0f,
    data[3], data[4], data[5], 0f,
    data[6], data[7], data[8], 0f,
    0f, 0f, 0f, 1f
)

fun Matrix4.copyFrom(that: Matrix2): Matrix4 = that.toMatrix4(this)
fun Matrix4.copyFrom(that: Matrix2d): Matrix4 = that.toMatrix4(this)
fun Matrix4.copyFrom(that: Matrix3): Matrix4 = that.toMatrix4(this)
