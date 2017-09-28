package com.soywiz.korma.math

impl object NativeMath {
	impl fun round(v: Double): Double = java.lang.Math.round(v).toDouble()
	impl fun ceil(v: Double): Double = java.lang.Math.ceil(v)
	impl fun floor(v: Double): Double = java.lang.Math.floor(v)
	impl fun pow(b: Double, e: Double): Double = java.lang.Math.pow(b, e)
	impl fun log(v: Double): Double = java.lang.Math.log(v)
	impl fun cos(v: Double): Double = java.lang.Math.cos(v)
	impl fun sin(v: Double): Double = java.lang.Math.sin(v)
	impl fun tan(v: Double): Double = java.lang.Math.tan(v)
	impl fun sqrt(v: Double): Double = java.lang.Math.sqrt(v)
	impl fun intBitsToFloat(v: Int): Float = java.lang.Float.intBitsToFloat(v)
	impl fun floatToIntBits(v: Float): Int = java.lang.Float.floatToIntBits(v)
	impl fun longBitsToDouble(v: Long): Double = java.lang.Double.longBitsToDouble(v)
	impl fun doubleToLongBits(v: Double): Long = java.lang.Double.doubleToLongBits(v)
}