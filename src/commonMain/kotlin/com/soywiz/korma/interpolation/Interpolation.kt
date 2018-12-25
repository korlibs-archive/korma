package com.soywiz.korma.interpolation

interface Interpolable<T> {
    fun interpolateWith(other: T, ratio: Float): T
}

interface MutableInterpolable<T> {
    fun setToInterpolated(l: T, r: T, ratio: Float): T
}

@Suppress("UNCHECKED_CAST", "USELESS_CAST")
fun <T> interpolateAny(min: T, max: T, ratio: Float): T = when (min) {
    is Double -> ratio.interpolate(min as Double, max as Double) as T
    is Int -> ratio.interpolate(min as Int, max as Int) as T
    is Float -> ratio.interpolate(min as Float, max as Float) as T
    is Long -> ratio.interpolate(min as Long, max as Long) as T
    is Interpolable<*> -> (min as Interpolable<Any>).interpolateWith(max as Interpolable<Any>, ratio) as T
    else -> throw IllegalArgumentException("Value is not interpolable")
}

fun Double.interpolate(l: Float, r: Float): Float = (l + (r - l) * this).toFloat()
fun Double.interpolate(l: Double, r: Double): Double = l + (r - l) * this
fun Double.interpolate(l: Int, r: Int): Int = (l + (r - l) * this).toInt()
fun Double.interpolate(l: Long, r: Long): Long = (l + (r - l) * this).toLong()
fun <T> Double.interpolate(l: Interpolable<T>, r: Interpolable<T>): T = l.interpolateWith(r as T, this.toFloat())
fun <T : Interpolable<T>> Double.interpolate(l: T, r: T): T = l.interpolateWith(r, this.toFloat())

fun Float.interpolate(l: Float, r: Float): Float = (l + (r - l) * this)
fun Float.interpolate(l: Double, r: Double): Double = l + (r - l) * this
fun Float.interpolate(l: Int, r: Int): Int = (l + (r - l) * this).toInt()
fun Float.interpolate(l: Long, r: Long): Long = (l + (r - l) * this).toLong()
fun <T> Float.interpolate(l: Interpolable<T>, r: Interpolable<T>): T = l.interpolateWith(r as T, this)
fun <T : Interpolable<T>> Float.interpolate(l: T, r: T): T = l.interpolateWith(r, this)
