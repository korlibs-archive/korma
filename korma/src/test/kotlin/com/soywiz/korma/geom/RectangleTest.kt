package com.soywiz.korma.geom

import org.junit.Assert
import org.junit.Test

class RectangleTest {
	@Test
	fun name() {
		val big = Rectangle.fromBounds(0, 0, 50, 50)
		val small = Rectangle.fromBounds(10, 10, 20, 20)
		val out = Rectangle.fromBounds(100, 10, 200, 20)
		Assert.assertTrue(small in big)
		Assert.assertTrue(big !in small)
		Assert.assertTrue(small == (small intersection big))
		Assert.assertTrue(small == (big intersection small))
		Assert.assertTrue(null == (big intersection out))
		Assert.assertTrue(small intersects big)
		Assert.assertTrue(big intersects small)
		Assert.assertFalse(big intersects out)
	}

	@Test
	fun name2() {
		val r1 = Rectangle(20, 0, 30, 10)
		val r2 = Rectangle(100, 0, 100, 50)
		val ro = r1.copy()
		ro.setToAnchoredRectangle(ro, Anchor.MIDDLE_CENTER, r2)
		//Assert.assertEquals(Rectangle(0, 0, 0, 0), r1)
		Assert.assertEquals(Rectangle(35, 20, 30, 10), ro)
	}
}