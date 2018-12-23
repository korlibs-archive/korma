package com.soywiz.korma.geom.bezier

import com.soywiz.korma.*
import kotlin.test.*

class BezierTest {
    @Test
    fun testLength() {
        assertEquals(100.0, Bezier(vec(0, 0), vec(50, 0), vec(100, 0)).length(steps = 100))
    }
}
