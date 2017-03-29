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
			"[Rectangle(20.0, 50.0, 20.0, 10.0), Rectangle(20.0, 20.0, 10.0, 30.0), Rectangle(0.0, 0.0, 100.0, 20.0), Rectangle(0.0, 20.0, 20.0, 80.0)]",
			result.toString()
		)
	}
}