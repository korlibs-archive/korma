package com.soywiz.korma.geom.triangle.triangulate

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.triangle.triangulate.*
import kotlin.test.*

class TriangulateTest {
    @Test
    fun test() {
        assertEquals(
            "[[Triangle((0, 100), (100, 0), (100, 100)), Triangle((0, 100), (0, 0), (100, 0))], [Triangle((300, 100), (400, 0), (400, 100)), Triangle((300, 100), (300, 0), (400, 0))]]",
            VectorPath {
                rect(0, 0, 100, 100)
                rect(300, 0, 100, 100)
            }.triangulate().toString()
        )
    }

    @Test
    fun test2() {
        val points = listOf(
            Point(3, 10),
            Point(1, 5),
            Point(3, 1),
            Point(4, 0),
            Point(6, 0)
        )

        assertEquals(
            "...",
            points.triangulate().toString()
        )
    }

    @Test
    fun test3() {
        val points = listOf(
            Point(-371.182, 307.381),
            Point(-365.909, 310.721),
            Point(-369.425, 318.455),
            Point(-373.468, 317.048),
            Point(-375.401, 315.818),
            Point(-376.28, 314.588),
            Point(-376.28, 313.357),
            Point(-375.753, 311.599),
            Point(-373.643, 307.733),
            Point(-372.764, 307.029),
            Point(-372.061, 307.029)
        )

        assertEquals(
            "...",
            points.triangulate().toString()
        )
    }
}
