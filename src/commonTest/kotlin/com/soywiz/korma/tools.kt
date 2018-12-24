package com.soywiz.korma

import kotlin.math.abs
import kotlin.test.assertTrue

inline fun assertEqualsDouble(l: Number, r: Number, delta: Number) {
    assertTrue(abs(l.toDouble() - r.toDouble()) < delta.toDouble(), message = "$l != $r :: delta=$delta")
}

