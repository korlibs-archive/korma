package com.soywiz.korma.geom

import kotlin.test.*

class Matrix3DTest {
    @Test
    fun testMatrix4() {
        val matrix = Matrix3D()
        val identityData = listOf(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        )
        assertEquals(identityData, matrix.data.map { it.toInt() })
        val matrix2 = matrix.clone().transpose()
        assertEquals(identityData, matrix2.data.map { it.toInt() })
    }

    @Test
    fun test2() {
        val mat = Matrix3D(
            1f, 2f, 3f,
            4f, 5f, 6f,
            7f, 8f, 9f
        ) * (-1)

        val floats = FloatArray(9)

        mat.copyToFloat3x3(floats)

        assertEquals(listOf(-1f, -2f, -3f, -4f, -5f, -6f, -7f, -8f, -9f), floats.toList())
    }

    @Test
    fun test3() {
        val mat = Matrix(2, 0, 0, 2, 20, 20)
        val mat4 = mat.toMatrix3D()
        assertEquals(Point(40, 40), mat.transform(Point(10, 10)))
        assertEquals(Vector3D(40, 40, 0), mat4.transform(Vector3D(10, 10, 0)))
    }
}
