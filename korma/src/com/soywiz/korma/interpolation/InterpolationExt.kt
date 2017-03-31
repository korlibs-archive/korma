package com.soywiz.korma.interpolation

fun Double.interpolate(l: Double, r: Double): Double = l + (r - l) * this
fun Double.interpolate(l: Int, r: Int): Int = (l + (r - l) * this).toInt()
fun <T : Interpolable<T>> Double.interpolate(l: T, r: T): T = l.interpolateWith(r, this)