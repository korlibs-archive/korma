package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.VectorPath
import org.junit.Assert
import org.junit.Test

class Shape2dTest {
	@Test
	fun name() {
		Assert.assertEquals(
			"Rectangle(x=5, y=0, width=5, height=10)",
			(Shape2d.Rectangle(0, 0, 10, 10) intersection Shape2d.Rectangle(5, 0, 10, 10)).toString()
		)

		Assert.assertEquals(
			"Complex(items=[Rectangle(x=10, y=0, width=5, height=10), Rectangle(x=0, y=0, width=5, height=10)])",
			(Shape2d.Rectangle(0, 0, 10, 10) xor Shape2d.Rectangle(5, 0, 10, 10)).toString()
		)
	}

	@Test
	fun extend() {
		Assert.assertEquals(
			"Rectangle(x=-10, y=-10, width=30, height=30)",
			(Shape2d.Rectangle(0, 0, 10, 10).extend(10.0)).toString()
		)
	}

	@Test
	fun name2() {
		val vp = VectorPath().apply { circle(0.0, 0.0, 100.0) }
		Assert.assertEquals(78, vp.toShape2d().paths.totalVertices)
	}
}