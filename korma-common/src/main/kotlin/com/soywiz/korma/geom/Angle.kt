package com.soywiz.korma.geom

import com.soywiz.korma.math.Math

object Angle {
	fun cos01(ratio: Double) = Math.cos(Math.PI * 2.0 * ratio)
	fun sin01(ratio: Double) = Math.sin(Math.PI * 2.0 * ratio)
}