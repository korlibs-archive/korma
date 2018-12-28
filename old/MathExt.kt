
///////////////////////////////////////////////////////
// TO REMOVE
///////////////////////////////////////////////////////
fun hypotNoSqrt(x: Double, y: Double): Double = (x * x + y * y)

inline fun pow(a: Number, b: Number) = a.toDouble().pow(b.toDouble())
fun log(a: Double): Double = ln(a)
fun Double.clampSpecial(min: Double, max: Double): Double = if (max >= min) this.clamp(min, max) else this
fun Double.clampf255(): Int = if (this < 0.0) 0 else if (this > 1.0) 255 else (this * 255).toInt()
fun Double.clampf01(): Double = if (this < 0.0) 0.0 else if (this > 1.0) 1.0 else this
fun Int.clampn255(): Int = if (this < -255) -255 else if (this > 255) 255 else this
fun Int.clamp255(): Int = if (this < 0) 0 else if (this > 255) 255 else this
fun packUintFast(r: Int, g: Int, b: Int, a: Int): Int = (a shl 24) or (b shl 16) or (g shl 8) or (r shl 0)
fun pack4fUint(r: Double, g: Double, b: Double, a: Double): Int =
    packUintFast(r.clampf255(), g.clampf255(), b.clampf255(), a.clampf255())

fun rintDouble(value: Double): Double {
    val twoToThe52 = 2.0.pow(52) // 2^52
    val sign = kotlin.math.sign(value) // preserve sign info
    var rvalue = kotlin.math.abs(value)
    if (rvalue < twoToThe52) rvalue = ((twoToThe52 + rvalue) - twoToThe52)
    return sign * rvalue // restore original sign
}

private fun handleCastInfinite(value: Double): Int = if (value < 0) -2147483648 else 2147483647

fun rintChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return rintDouble(value.toDouble()).toInt()
}

fun castChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return if (value < 0) kotlin.math.ceil(value).toInt() else kotlin.math.floor(value).toInt()
}

fun truncChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return if (value < 0) kotlin.math.ceil(value).toInt() else kotlin.math.floor(value).toInt()
}

fun roundChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.round(value).toInt()
}

fun floorChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.floor(value).toInt()
}

fun ceilChecked(value: Double): Int {
    if (value.isNanOrInfinite()) return handleCastInfinite(value)
    return kotlin.math.ceil(value).toInt()
}

fun multiplyIntegerUnsigned(a: Int, b: Int) = (a * b) or 0
fun multiplyIntegerSigned(a: Int, b: Int): Int = (a * b) or 0
fun divideIntegerUnsigned(a: Int, b: Int): Int = (a / b) or 0
fun divideIntegerSigned(a: Int, b: Int): Int = (a / b) or 0
