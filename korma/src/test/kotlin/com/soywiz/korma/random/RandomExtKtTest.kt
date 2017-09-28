package com.soywiz.korma.random

import org.junit.Assert
import org.junit.Test

class RandomExtKtTest {
	//class TestRandom(val values: List<Int>) : Random() {
	//	var index = 0
	//	//override fun next(bits: Int): Int = values.getCyclic(index++) and ((1L shl bits) - 1).toInt()
	//	override fun next(bits: Int): Int {
	//		return values.getCyclic(index++)
	//	}
	//}

	@Test
	fun weighted() {
		val random = MtRand(0L)
		val weighted = mapOf("a" to 1, "b" to 1)
		random.nextInt()
		Assert.assertEquals("b", random[weighted])
		Assert.assertEquals("b", random[weighted])
		Assert.assertEquals("a", random[weighted])
		Assert.assertEquals("a", random[weighted])
	}

	@Test
	fun weighted2() {
		val weighted = mapOf("a" to 1, "b" to 2)
		val random = MtRand(0L)
		Assert.assertEquals("b", random[weighted])
		Assert.assertEquals("a", random[weighted])
		Assert.assertEquals("a", random[weighted])
		Assert.assertEquals("b", random[weighted])
		Assert.assertEquals("b", random[weighted])
	}
}