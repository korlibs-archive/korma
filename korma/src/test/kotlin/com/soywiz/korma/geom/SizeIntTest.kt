package com.soywiz.korma.geom

import org.junit.Assert
import org.junit.Test

class SizeIntTest {
	@Test
	fun cover() {
		Assert.assertEquals(SizeInt(100, 400), SizeInt(50, 200).applyScaleMode(container = SizeInt(100, 100), mode = ScaleMode.COVER))
		Assert.assertEquals(SizeInt(25, 100), SizeInt(50, 200).applyScaleMode(container = SizeInt(25, 25), mode = ScaleMode.COVER))
	}
}