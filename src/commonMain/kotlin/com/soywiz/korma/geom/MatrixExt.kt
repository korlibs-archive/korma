package com.soywiz.korma.geom

fun Matrix3D.copyFrom(that: IMatrix): Matrix3D = that.toMatrix3D(this)

fun IMatrix.toMatrix3D(out: Matrix3D = Matrix3D()): Matrix3D = out.setTo(
    a, b, 0, 0,
    c, d, 0, 0,
    0, 0, 1, 0,
    tx, ty, 0, 1
)
