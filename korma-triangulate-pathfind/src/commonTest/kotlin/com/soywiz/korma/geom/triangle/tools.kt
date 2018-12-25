package com.soywiz.korma.geom.triangle

import kotlin.math.abs
import kotlin.test.assertTrue

fun assertEqualsNumber(l: Float, r: Float, delta: Float) {
    assertTrue(abs(l - r) < delta, message = "$l != $r :: delta=$delta")
}

fun assertEqualsNumber(l: Double, r: Double, delta: Double) {
    assertTrue(abs(l - r) < delta, message = "$l != $r :: delta=$delta")
}
