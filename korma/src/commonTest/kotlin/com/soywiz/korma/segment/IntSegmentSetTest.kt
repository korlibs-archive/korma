package com.soywiz.korma.segment

import kotlin.test.*

class IntSegmentSetTest {
    @Test
    fun testAdd() {
        assertEquals("[]", IntSegmentSet().toString())
        assertEquals("[0-10]", IntSegmentSet().add(0, 10).toString())
        assertEquals("[0-10, 11-20]", IntSegmentSet().add(0, 10).add(11, 20).toString())
        assertEquals("[0-20]", IntSegmentSet().add(0, 10).add(10, 20).toString())
        assertEquals("[0-20]", IntSegmentSet().add(0, 10).add(11, 20).add(10, 11).toString())
        assertEquals("[0-10, 11-19, 20-30]", IntSegmentSet().add(0, 10).add(20, 30).add(11, 19).toString())
    }

    @Test
    fun testIntersection() {
        assertEquals("[5-10]", IntSegmentSet().setToIntersect(IntSegmentSet().add(0, 10), IntSegmentSet().add(5, 15)).toString())
        assertEquals("[5-10, 12-15]", IntSegmentSet().setToIntersect(IntSegmentSet().add(0, 10).add(12, 20), IntSegmentSet().add(5, 15)).toString())
    }
}
