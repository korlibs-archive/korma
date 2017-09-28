package com.soywiz.korma.geom

import org.junit.Assert
import org.junit.Test

class RectangleIntTest {
	@Test
	fun name() {
		Assert.assertEquals(SizeInt(25, 100), SizeInt(50, 200).fitTo(container = SizeInt(100, 100)))
		Assert.assertEquals(SizeInt(50, 200), SizeInt(50, 200).fitTo(container = SizeInt(100, 200)))
	}
}