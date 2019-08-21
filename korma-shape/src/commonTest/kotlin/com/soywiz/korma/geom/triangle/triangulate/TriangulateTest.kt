package com.soywiz.korma.geom.triangle.triangulate

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
}
