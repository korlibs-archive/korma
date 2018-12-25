package com.soywiz.korma.internal

internal val Float.niceStr: String get() = if (this.toLong().toFloat() == this) "${this.toLong()}" else "$this"
internal val Double.niceStr: String get() = if (this.toLong().toDouble() == this) "${this.toLong()}" else "$this"

internal infix fun Float.umod(other: Float): Float {
    val remainder = this % other
    return when {
        remainder < 0 -> remainder + other
        else -> remainder
    }
}
