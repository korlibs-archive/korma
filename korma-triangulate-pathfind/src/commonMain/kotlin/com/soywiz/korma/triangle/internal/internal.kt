package com.soywiz.korma.triangle.internal

internal val Float.niceStr2: String get() = if (this.toLong().toFloat() == this) "${this.toLong()}" else "$this"
internal val Double.niceStr2: String get() = if (this.toLong().toDouble() == this) "${this.toLong()}" else "$this"

internal object Constants {
    const val EPSILON: Float = 1e-12f
    const val PI: Float = kotlin.math.PI.toFloat()
    const val PI_2: Float = PI / 2f
    const val PI_3div4: Float = 3 * PI / 4
}
