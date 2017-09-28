package com.soywiz.korma.math

import org.khronos.webgl.*

impl object NativeMath {
	val buffer = ArrayBuffer(8)
	val i32 = Int32Array(buffer)
	val f32 = Float32Array(buffer)
	val f64 = Float64Array(buffer)

	impl fun intBitsToFloat(v: Int): Float {
		i32[0] = v
		return f32[0]
	}
	impl fun floatToIntBits(v: Float): Int {
		f32[0] = v
		return i32[0]
	}
	impl fun longBitsToDouble(v: Long): Double {
		i32[0] = (v ushr 0).toInt()
		i32[1] = (v ushr 32).toInt()
		return f64[0]
	}
	impl fun doubleToLongBits(v: Double): Long {
		f64[0] = v
		val low = i32[0]
		val high = i32[1]
		return (low.toLong() and 0xFFFFFFFFL) or (high.toLong() shl 32)
	}

	impl fun round(v: Double): Double = kotlin.js.Math.round(v).toDouble()
	impl fun ceil(v: Double): Double = kotlin.js.Math.ceil(v).toDouble()
	impl fun floor(v: Double): Double = kotlin.js.Math.floor(v).toDouble()
	impl fun pow(b: Double, e: Double): Double = kotlin.js.Math.pow(b, e)
	impl fun log(v: Double): Double = kotlin.js.Math.log(v)
	impl fun cos(v: Double): Double = kotlin.js.Math.cos(v)
	impl fun sin(v: Double): Double = kotlin.js.Math.sin(v)
	impl fun tan(v: Double): Double = kotlin.js.Math.tan(v)
	impl fun sqrt(v: Double): Double = kotlin.js.Math.sqrt(v)

}