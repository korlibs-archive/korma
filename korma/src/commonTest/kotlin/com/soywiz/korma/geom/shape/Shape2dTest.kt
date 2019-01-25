package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.vector.*
import kotlin.test.*

class Shape2dTest {
    @Test
    fun test() {
        assertEquals(
            "Rectangle(x=0, y=0, width=100, height=100)",
            VectorPath { rect(0, 0, 100, 100) }.toShape2d(closed = true).toString()
        )

        assertEquals(
            "Complex(items=[Rectangle(x=0, y=0, width=100, height=100), Rectangle(x=300, y=0, width=100, height=100)])",
            VectorPath {
                rect(0, 0, 100, 100)
                rect(300, 0, 100, 100)
            }.toShape2d(closed = true).toString()
        )

        assertEquals(
            "Polygon(points=[(0, 0), (100, 0), (100, 100)])",
            VectorPath {
                moveTo(0, 0)
                lineTo(100, 0)
                lineTo(100, 100)
                close()
            }.toShape2d(closed = true).toString()
        )
    }
}
