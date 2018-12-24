package com.soywiz.korma.geom

import com.soywiz.korma.Vector2
import com.soywiz.korma.length
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals

class Vector2Test {
    @Test
    fun name() {
        val v = Vector2(1, 1.0)
        //assertEquals(sqrt(2.0), v.length, 0.001)
        assertEquals(sqrt(2f), v.length)
    }

    @Test
    fun testString() {
        assertEquals("(1, 2)", Vector2(1, 2).toString())
    }
}
