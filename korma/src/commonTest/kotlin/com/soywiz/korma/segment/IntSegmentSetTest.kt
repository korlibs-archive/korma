package com.soywiz.korma.segment

import com.soywiz.korma.annotations.*
import kotlin.test.*

@OptIn(KormaExperimental::class)
class IntSegmentSetTest {
    val segment0 get() = IntSegmentSet().clone()
    val segment1 get() = IntSegmentSet().addUnsafe(0, 10).clone()
    val segment2 get() = IntSegmentSet().addUnsafe(0, 10).addUnsafe(12, 20).clone()
    val segment3 get() = IntSegmentSet().addUnsafe(0, 10).addUnsafe(20, 30).addUnsafe(50, 60).clone()
    val segment4 get() = IntSegmentSet().addUnsafe(0, 10).addUnsafe(20, 30).addUnsafe(50, 60).addUnsafe(70, 80).clone()

    val IntSegmentSet.str get() = this.toString()

    @Test
    fun testAdd() {
        assertEquals("[]", segment0.str)
        assertEquals("[0-10]", segment1.str)
        assertEquals("[0-10, 11-20]", segment1.add(11, 20).str)
        assertEquals("[0-20]", segment1.add(10, 20).str)
        assertEquals("[0-20]", segment1.add(11, 20).add(10, 11).str)
        assertEquals("[0-10, 11-19, 20-30]", segment1.add(20, 30).add(11, 19).str)
    }

    @Test
    fun testHolesAdd3() {
        assertEquals("[-10--5, 0-10, 20-30, 50-60]", segment3.add(-10, -5).str)
        assertEquals("[0-10, 12-18, 20-30, 50-60]", segment3.add(12, 18).str)
        assertEquals("[0-10, 20-30, 32-48, 50-60]", segment3.add(32, 48).str)
        assertEquals("[0-10, 20-30, 50-60, 62-70]", segment3.add(62, 70).str)
    }

    @Test
    fun testHolesAdd4() {
        assertEquals("[-10--5, 0-10, 20-30, 50-60, 70-80]", segment4.add(-10, -5).str)
        assertEquals("[0-10, 12-18, 20-30, 50-60, 70-80]", segment4.add(12, 18).str)
        assertEquals("[0-10, 20-30, 32-48, 50-60, 70-80]", segment4.add(32, 48).str)
        assertEquals("[0-10, 20-30, 50-60, 62-68, 70-80]", segment4.add(62, 68).str)
        assertEquals("[0-10, 20-30, 50-60, 70-80, 82-90]", segment4.add(82, 90).str)
    }

    @Test
    fun testMiddleCombineAdd3() {
        assertEquals("[0-10, 20-30, 50-60]", segment3.add(20, 30).str)
        assertEquals("[0-10, 20-48, 50-60]", segment3.add(25, 48).str)
    }

    @Test
    fun testLeftAdd3() {
        assertEquals("[-10-10, 20-30, 50-60]", segment3.add(-10, +5).str)
        assertEquals("[-10-30, 50-60]", segment3.add(-10, +25).str)
        assertEquals("[-10-45, 50-60]", segment3.add(-10, +45).str)
        assertEquals("[-10-60]", segment3.add(-10, +50).str)
        assertEquals("[-10-60]", segment3.add(-10, +55).str)
        assertEquals("[-10-70]", segment3.add(-10, +70).str)
    }

    @Test
    fun testLeftAdd4() {
        assertEquals("[-20--10, 0-10, 20-30, 50-60, 70-80]", segment4.add(-20, -10).str)
        assertEquals("[-10-10, 20-30, 50-60, 70-80]", segment4.add(-10, +5).str)
        assertEquals("[-10-30, 50-60, 70-80]", segment4.add(-10, +25).str)
        assertEquals("[-10-45, 50-60, 70-80]", segment4.add(-10, +45).str)
        assertEquals("[-10-60, 70-80]", segment4.add(-10, +50).str)
        assertEquals("[-10-60, 70-80]", segment4.add(-10, +55).str)
        assertEquals("[-10-80]", segment4.add(-10, +70).str)
        assertEquals("[-10-80]", segment4.add(-10, +75).str)
        assertEquals("[-10-90]", segment4.add(-10, +90).str)
    }

    @Test
    fun testRightAdd3() {
        assertEquals("[0-10, 20-30, 50-60, 75-80]", segment3.add(75, 80).str)
        assertEquals("[0-10, 20-30, 50-80]", segment3.add(60, 80).str)
        assertEquals("[0-10, 20-30, 45-80]", segment3.add(45, 80).str)
        assertEquals("[0-10, 20-80]", segment3.add(30, 80).str)
        assertEquals("[0-10, 20-80]", segment3.add(25, 80).str)
        assertEquals("[0-10, 15-80]", segment3.add(15, 80).str)
        assertEquals("[0-80]", segment3.add(10, 80).str)
        assertEquals("[0-80]", segment3.add(5, 80).str)
        assertEquals("[0-80]", segment3.add(0, 80).str)
        assertEquals("[-5-80]", segment3.add(-5, 80).str)
    }

    @Test
    fun testRightAdd4() {
        assertEquals("[0-10, 20-30, 50-60, 70-80, 85-90]", segment4.add(85, 90).str)
        assertEquals("[0-10, 20-30, 50-60, 70-90]", segment4.add(80, 90).str)
        assertEquals("[0-10, 20-30, 50-60, 70-90]", segment4.add(75, 90).str)
        assertEquals("[0-10, 20-30, 50-60, 70-90]", segment4.add(70, 90).str)
        assertEquals("[0-10, 20-30, 50-60, 65-90]", segment4.add(65, 90).str)
        assertEquals("[0-10, 20-30, 50-90]", segment4.add(60, 90).str)
        assertEquals("[0-10, 20-30, 50-90]", segment4.add(55, 90).str)
        assertEquals("[0-10, 20-30, 50-90]", segment4.add(50, 90).str)
        assertEquals("[0-10, 20-30, 45-90]", segment4.add(45, 90).str)
        assertEquals("[0-10, 20-90]", segment4.add(30, 90).str)
        assertEquals("[0-10, 20-90]", segment4.add(25, 90).str)
        assertEquals("[0-10, 20-90]", segment4.add(20, 90).str)
        assertEquals("[0-10, 15-90]", segment4.add(15, 90).str)
        assertEquals("[0-90]", segment4.add(10, 90).str)
        assertEquals("[0-90]", segment4.add(5, 90).str)
        assertEquals("[0-90]", segment4.add(0, 90).str)
        assertEquals("[-10-90]", segment4.add(-10, 90).str)
    }

    fun intersect(a: IntSegmentSet, b: IntSegmentSet) = IntSegmentSet().setToIntersect(a, b)

    @Test
    fun testIntersection() {
        assertEquals("[5-10]", intersect(segment1, IntSegmentSet().add(5, 15)).str)
        assertEquals("[5-10, 12-15]", intersect(segment2, IntSegmentSet().add(5, 15)).str)
    }
}
