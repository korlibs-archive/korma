package com.soywiz.korma.internal

import com.soywiz.korma.math.*
import kotlin.math.*

internal val Float.niceStr: String get() = if (almostEquals(this.toLong().toFloat(), this)) "${this.toLong()}" else "$this"
internal val Double.niceStr: String get() = if (almostEquals(this.toLong().toDouble(), this)) "${this.toLong()}" else "$this"

internal infix fun Double.umod(other: Double): Double {
    val remainder = this % other
    return when {
        remainder < 0 -> remainder + other
        else -> remainder
    }
}

internal fun floorCeil(v: Double): Double = if (v < 0.0) ceil(v) else floor(v)
