package com.soywiz.korma.math

import com.soywiz.korma.geom.*
import kotlin.math.*

fun Long.clamp(min: Long, max: Long): Long = if (this < min) min else if (this > max) max else this
fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Double.clamp(min: Double, max: Double): Double = if (this < min) min else if (this > max) max else this
fun Double.clampSpecial(min: Double, max: Double): Double = if (max >= min) this.clamp(min, max) else this
fun Float.clamp(min: Float, max: Float): Float = if (this < min) min else if (this > max) max else this
fun Int.clampInt(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
fun Double.clampf255(): Int = if (this < 0.0) 0 else if (this > 1.0) 255 else (this * 255).toInt()
fun Double.clampf01(): Double = if (this < 0.0) 0.0 else if (this > 1.0) 1.0 else this
fun Int.clampn255(): Int = if (this < -255) -255 else if (this > 255) 255 else this
fun Int.clamp255(): Int = if (this < 0) 0 else if (this > 255) 255 else this

fun Double.betweenInclusive(min: Double, max: Double): Boolean = (this >= min) && (this <= max)

fun multiplyIntegerUnsigned(a: Int, b: Int) = (a * b) or 0
fun multiplyIntegerSigned(a: Int, b: Int): Int = (a * b) or 0
fun divideIntegerUnsigned(a: Int, b: Int): Int = (a / b) or 0
fun divideIntegerSigned(a: Int, b: Int): Int = (a / b) or 0

fun distance(a: Double, b: Double): Double = kotlin.math.abs(a - b)
fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double = kotlin.math.hypot(x1 - x2, y1 - y2)
inline fun distance(x1: Number, y1: Number, x2: Number, y2: Number): Double = distance(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())

fun distance(a: IPoint, b: IPoint): Double = distance(a.x, a.y, b.x, b.y)
fun distance(a: IPointInt, b: IPointInt): Double = distance(a.x, a.y, b.x, b.y)
fun distancePoint(a: IPoint, b: IPoint): Double = distance(a.x, a.y, b.x, b.y)

fun roundDecimalPlaces(value: Double, places: Int): Double {
    val placesFactor: Double = 10.0.pow(places.toDouble())
    return kotlin.math.round(value * placesFactor) / placesFactor
}

fun isEquivalent(a: Double, b: Double, epsilon: Double = 0.0001): Boolean = (a - epsilon < b) && (a + epsilon > b)

fun smoothstep(edge0: Double, edge1: Double, step: Double): Double {
    val v = (step - edge0) / (edge1 - edge0)
    val step2 = v.clamp(0.0, 1.0)
    return step2 * step2 * (3 - 2 * step2)
}

fun convertRange(value: Double, minSrc: Double, maxSrc: Double, minDst: Double, maxDst: Double): Double = (((value - minSrc) / (maxSrc - minSrc)) * (maxDst - minDst)) + minDst

fun log2(v: Int): Int = log(v.toDouble(), base = 2.0).toInt()
fun log10(v: Int): Int = log(v.toDouble(), base = 10.0).toInt()

fun signNonZeroM1(x: Double): Int = if (x <= 0) -1 else +1
fun signNonZeroP1(x: Double): Int = if (x >= 0) +1 else -1

private fun Float.isAlmostZero(): Boolean = kotlin.math.abs(this) <= 1e-19
private fun Float.isNanOrInfinite() = this.isNaN() || this.isInfinite()

private fun handleCastInfinite(value: Float): Int = if (value < 0) -2147483648 else 2147483647

fun rintDouble(value: Double): Double {
    val twoToThe52 = 2.0.pow(52) // 2^52
    val sign = kotlin.math.sign(value) // preserve sign info
    var rvalue = kotlin.math.abs(value)
    if (rvalue < twoToThe52) rvalue = ((twoToThe52 + rvalue) - twoToThe52)
    return sign * rvalue // restore original sign
}

fun rintChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return rintDouble(value.toDouble()).toInt()
}

fun castChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return if (value < 0) kotlin.math.ceil(value).toInt() else kotlin.math.floor(value).toInt()
}

fun truncChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return if (value < 0) kotlin.math.ceil(value).toInt() else kotlin.math.floor(value).toInt()
}

fun roundChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.round(value).toInt()
}

fun floorChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.floor(value).toInt()
}

fun ceilChecked(value: Float): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.ceil(value).toInt()
}

fun hypotNoSqrt(x: Double, y: Double): Double = (x * x + y * y)
inline fun pow(a: Number, b: Number) = a.toDouble().pow(b.toDouble())
fun log(a: Double): Double = log(a, E)

fun packUintFast(r: Int, g: Int, b: Int, a: Int): Int = (a shl 24) or (b shl 16) or (g shl 8) or (r shl 0)
fun pack4fUint(r: Double, g: Double, b: Double, a: Double): Int =
    packUintFast(r.clampf255(), g.clampf255(), b.clampf255(), a.clampf255())
