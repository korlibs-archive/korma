package com.soywiz.korma.geom

import com.soywiz.korma.Vector2
import org.junit.Assert
import org.junit.Test

class Vector2Test {
	@Test
	fun name() {
		val v = Vector2(1, 1.0)
		Assert.assertEquals(Math.sqrt(2.0), v.length, 0.001)
	}
}