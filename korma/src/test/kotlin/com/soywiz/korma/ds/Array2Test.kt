package com.soywiz.korma.ds

import org.junit.Assert
import org.junit.Test

class Array2Test {
	@Test
	fun name() {
		val map = Array2.fromString(Tiles.MAPPING, -1, """
			:    #####
			:    #   #
			:    #$  #
			:  ###  $##
			:  #  $ $ #
			:### # ## #   ######
			:#   # ## #####  ..#
			:# $  $         *..#
			:##### ### #@##  ..#
			:    #     #########
			:    #######
		""")


		val output = map.toString(Tiles.REV_MAPPING, margin = ":")

		val expected = listOf(
				":     #####          ",
				":     #   #          ",
				":     #$  #          ",
				":   ###  $##         ",
				":   #  $ $ #         ",
				": ### # ## #   ######",
				": #   # ## #####  ..#",
				": # $  $         *..#",
				": ##### ### #@##  ..#",
				":     #     #########",
				":     #######        "
		).joinToString("\n")

		Assert.assertEquals(expected, output)
	}

	object Tiles {
		const val GROUND = 0
		const val WALL = 1
		const val CONTAINER = 2
		const val BOX = 3
		const val BOX_OVER = 4
		const val CHARACTER = 10

		val AVAILABLE = setOf(Tiles.GROUND, Tiles.CONTAINER)
		val BOXLIKE = setOf(Tiles.BOX, Tiles.BOX_OVER)

		val MAPPING = mapOf(
				' ' to Tiles.GROUND,
				'#' to Tiles.WALL,
				'.' to Tiles.CONTAINER,
				'$' to Tiles.BOX,
				'*' to Tiles.BOX_OVER,
				'@' to Tiles.CHARACTER
		)

		val REV_MAPPING = MAPPING.map { it.value to it.key }.toMap()
	}
}