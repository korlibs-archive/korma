package com.soywiz.korma.random

interface RandomBase {
	val maxValue: Int
	fun seed(s: Int): RandomBase
	fun getNextValue(): Int
}