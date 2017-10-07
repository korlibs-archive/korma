package com.soywiz.korma.ds

import org.junit.Test
import kotlin.test.assertEquals

class IntArrayListTest {
	@Test
	fun name() {
		val values = IntArrayList(2)
		assertEquals(0, values.length)
		assertEquals(2, values.capacity)
		values.add(1)
		assertEquals(listOf(1), values.toList())
		assertEquals(1, values.length)
		assertEquals(2, values.capacity)
		values.add(2)
		assertEquals(listOf(1, 2), values.toList())
		assertEquals(2, values.length)
		assertEquals(2, values.capacity)
		values.add(3)
		assertEquals(listOf(1, 2, 3), values.toList())
		assertEquals(3, values.length)
		assertEquals(6, values.capacity)
	}
}