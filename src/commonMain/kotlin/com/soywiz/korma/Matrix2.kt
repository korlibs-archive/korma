package com.soywiz.korma

class Matrix2(
    val data: FloatArray = floatArrayOf(
        1f, 0f,
        0f, 1f
    )
) {
    var a: Float set(value) = run { data[0] = value }; get() = data[0]
    var b: Float set(value) = run { data[1] = value }; get() = data[1]
    var c: Float set(value) = run { data[2] = value }; get() = data[2]
    var d: Float set(value) = run { data[3] = value }; get() = data[3]

    fun index(x: Int, y: Int) = y * 2 + x
    operator fun get(x: Int, y: Int): Float = data[index(x, y)]
    operator fun set(x: Int, y: Int, value: Float) = run { data[index(x, y)] = value }
}
