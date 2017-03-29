package com.soywiz.korim.math

fun Double.interpolate(l: Double, r: Double): Double = l + (r - l) * this
fun Double.interpolate(l: Int, r: Int): Int = (l + (r - l) * this).toInt()