package com.soywiz.korma.geom.binpack

import com.soywiz.korma.geom.Size
import org.junit.Assert
import org.junit.Test

class BinPackTest {
	@Test
	fun name() {
		val factory = BinPacker(100.0, 100.0)
		val result = factory.addBatch(listOf(Size(20, 10), Size(10, 30), Size(100, 20), Size(20, 80)))
		//result.filterNotNull().render().showImageAndWaitExt()
		Assert.assertEquals(
			"[Rectangle(x=20, y=50, width=20, height=10), Rectangle(x=20, y=20, width=10, height=30), Rectangle(x=0, y=0, width=100, height=20), Rectangle(x=0, y=20, width=20, height=80)]",
			result.toString()
		)
	}
}