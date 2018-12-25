package com.soywiz.korma.geom

import com.soywiz.korma.internal.umod
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

inline class Angle(val radians: Float) {
    override fun toString(): String = "Angle($degrees)"

    companion object {
        fun fromRadians(rad: Float): Angle = Angle(rad)
        fun fromDegrees(deg: Float): Angle = Angle(deg2rad(deg))

        inline fun fromRadians(rad: Number) = fromRadians(rad.toFloat())
        inline fun fromDegrees(deg: Number) = fromDegrees(deg.toFloat())

        const val PI = kotlin.math.PI.toFloat()
        const val PI2 = PI * 2

        const val DEG2RAD = PI / 180f
        const val RAD2DEG = 180f / PI

        const val MAX_DEGREES = 360f
        const val MAX_RADIANS = PI2

        const val HALF_DEGREES = MAX_DEGREES / 2f
        const val HALF_RADIANS = MAX_RADIANS / 2f

        fun cos01(ratio: Float) = kotlin.math.cos(PI2 * ratio)
        fun sin01(ratio: Float) = kotlin.math.sin(PI2 * ratio)
        fun deg2rad(deg: Float) = deg * DEG2RAD
        fun rad2deg(rad: Float) = rad * RAD2DEG

        fun degreesToRadians(deg: Float): Float = deg * DEG2RAD
        fun radiansToDegrees(rad: Float): Float = rad * RAD2DEG

        fun degreesToRadians(deg: Double): Double = deg * DEG2RAD
        fun radiansToDegrees(rad: Double): Double = rad * RAD2DEG

        fun toRadians(v: Float): Float = v / 180f * PI
        fun toDegrees(v: Float): Float = v * 180f / PI

        fun toRadians(v: Double): Double = v / 180.0 * kotlin.math.PI
        fun toDegrees(v: Double): Double = v * 180.0 / kotlin.math.PI

        fun shortRadDistanceTo(fromRad: Float, toRad: Float): Float {
            val r0 = fromRad umod MAX_RADIANS
            val r1 = toRad umod MAX_RADIANS
            val diff = (r1 - r0 + HALF_RADIANS) % MAX_RADIANS - HALF_RADIANS
            return if (diff < -HALF_RADIANS) diff + MAX_RADIANS else diff
        }

        fun betweenRad(x0: Float, y0: Float, x1: Float, y1: Float): Float {
            val angle = atan2(y1 - y0, x1 - x0)
            return if (angle < 0) angle + PI2 else angle
        }

        fun between(x0: Float, y0: Float, x1: Float, y1: Float): Angle =
            Angle.fromRadians(betweenRad(x0, y0, x1, y1))

        fun betweenRad(p0: IPoint, p1: IPoint): Float = betweenRad(p0.x, p0.y, p1.x, p1.y)
        fun between(p0: IPoint, p1: IPoint): Angle = Angle.fromRadians(betweenRad(p0, p1))
    }
}

val Angle.degrees get() = Angle.rad2deg(radians)

//val normalizedRadians get() = KdsExt { radians umod Angle.MAX_RADIANS }
//val normalizedDegrees get() = KdsExt { degrees umod Angle.MAX_DEGREES }
val Angle.absoluteValue: Angle get() = Angle.fromRadians(radians.absoluteValue)

fun Angle.shortDistanceTo(other: Angle): Angle = Angle(Angle.shortRadDistanceTo(this.radians, other.radians))
inline operator fun Angle.times(scale: Number): Angle = Angle(this.radians * scale.toFloat())
inline operator fun Angle.div(scale: Number): Angle = Angle(this.radians / scale.toFloat())
inline operator fun Angle.plus(other: Angle): Angle = Angle(this.radians + other.radians)
inline operator fun Angle.minus(other: Angle): Angle = shortDistanceTo(other)
inline operator fun Angle.unaryMinus(): Angle = Angle(-radians)
inline operator fun Angle.unaryPlus(): Angle = Angle(+radians)

val Angle.normalizedRadians get() = radians umod Angle.MAX_RADIANS
val Angle.normalizedDegrees get() = degrees umod Angle.MAX_DEGREES
val Angle.normalized get() = Angle(radians umod Angle.MAX_RADIANS)

inline val Number.degrees get() = Angle.fromDegrees(this)
inline val Number.radians get() = Angle.fromRadians(this)
