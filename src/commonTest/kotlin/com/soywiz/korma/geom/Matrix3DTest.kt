package com.soywiz.korma.geom

import kotlin.test.*

class Matrix3DTest {
    @Test
    fun testToString() {
        val mat = Matrix3D.fromRows(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        assertEquals(
            """
            Matrix3D(
              [ 1, 2, 3, 4 ],
              [ 5, 6, 7, 8 ],
              [ 9, 10, 11, 12 ],
              [ 13, 14, 15, 16 ],
            )
			""".trimIndent(),
            mat.toString()
        )
    }

    @Test
    fun testMultiply() {
        val l = Matrix3D.fromRows(
            1, 2, 3, 4,
            5, 6, 7, 8,
            9, 10, 11, 12,
            13, 14, 15, 16
        )
        val r = Matrix3D.fromRows(
            17, 18, 19, 20,
            21, 22, 23, 24,
            25, 26, 27, 28,
            29, 30, 31, 32
        )
        assertEquals(
            Matrix3D.fromRows(
                250,    260,    270,    280,
                618,    644,    670,    696,
                986,    1028,   1070,   1112,
                1354,   1412,   1470,   1528

            ),
            (l * r)
        )
    }

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
        val mat = Matrix3D.fromRows(
            1f, 2f, 3f, 0f,
            4f, 5f, 6f, 0f,
            7f, 8f, 9f, 0f,
            0f, 0f, 0f, 1f
        ) * (-1)

        val floats = FloatArray(9)

        mat.copyToFloat3x3(floats, MajorOrder.ROW)

        assertEquals(listOf(-1f, -2f, -3f, -4f, -5f, -6f, -7f, -8f, -9f), floats.toList())
    }

    @Test
    fun test3() {
        run {
            val mat = Matrix(2, 0, 0, 2, 20, 20)
            val mat4 = mat.toMatrix3D()
            assertEquals(Point(40, 40), mat.transform(Point(10, 10)))
            assertEquals(Vector3D(40, 40, 0), mat4.transform(Vector3D(10f, 10f, 0f)))
        }
        run {
            val mat = Matrix(1, 2, 3, 4, 5, 6)
            val mat4 = mat.toMatrix3D()
            assertEquals(Point(45, 66), mat.transform(Point(10, 10)))
            assertEquals(Vector3D(45, 66, 0), mat4.transform(Vector3D(10f, 10f, 0f)))
        }
    }

    @Test
    fun ortho() {
        run {
            val projection = Matrix3D().setToOrtho(0f, 0f, 200f, 100f, 0f, -20f)
            assertEquals(Vector3D(0, 0, -1), Vector3D(100f, 50f, 0f).transform(projection))
            assertEquals(Vector3D(0, 0, +1), Vector3D(100f, 50f, 20f).transform(projection))
        }
        run {
            val projection = Matrix3D().setToOrtho(0f, 0f, 200f, 100f, 0f, +20f)
            assertEquals(Vector3D(0, 0, -1), Vector3D(100f, 50f, 0f).transform(projection))
            assertEquals(Vector3D(0, 0, +1), Vector3D(100f, 50f, -20f).transform(projection))
        }
    }

    @Test
    fun translation() {
        assertEquals(Vector3D(11f, 22f, 33f), Vector3D(10f, 20f, 30f).transform(Matrix3D().setToTranslation(1f, 2f, 3f)))
    }

    @Test
    fun scale() {
        assertEquals(Vector3D(100f, 400f, 900f), Vector3D(10f, 20f, 30f).transform(Matrix3D().setToScale(10f, 20f, 30f)))
    }

    @Test
    fun rotation() {
        assertEquals(Vector3D(0, 10, 0), Vector3D(10, 0, 0).transform(Matrix3D().setToRotationZ(90.degrees)))
        assertEquals(Vector3D(-10, 0, 0), Vector3D(10, 0, 0).transform(Matrix3D().setToRotationZ(180.degrees)))
        assertEquals(Vector3D(0, 10, 0), Vector3D(10, 0, 0).transform(Matrix3D().setToRotation(90.degrees, Vector3D(0, 0, 1))))
    }
}
