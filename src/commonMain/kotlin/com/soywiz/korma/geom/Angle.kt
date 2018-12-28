package com.soywiz.korma.geom

import com.soywiz.korma.internal.umod
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.atan2

inline class Angle(val radians: Double) {
    override fun toString(): String = "Angle($degrees)"

    companion object {
        fun fromRadians(radians: Double): Angle = Angle(radians)
        fun fromDegrees(degrees: Double): Angle = Angle(degreesToRadians(degrees))

        inline fun fromRadians(radians: Number) = fromRadians(radians.toDouble())
        inline fun fromDegrees(degrees: Number) = fromDegrees(degrees.toDouble())

        const val PI2 = PI * 2

        const val DEG2RAD = PI / 180.0
        const val RAD2DEG = 180.0 / PI

        const val MAX_DEGREES = 360.0
        const val MAX_RADIANS = PI2

        const val HALF_DEGREES = MAX_DEGREES / 2f
        const val HALF_RADIANS = MAX_RADIANS / 2f

        fun cos01(ratio: Double) = kotlin.math.cos(PI2 * ratio)
        fun sin01(ratio: Double) = kotlin.math.sin(PI2 * ratio)

        fun degreesToRadians(degrees: Double): Double = degrees * DEG2RAD
        fun radiansToDegrees(radians: Double): Double = radians * RAD2DEG

        fun shortDistanceTo(from: Angle, to: Angle): Angle {
            val r0 = from.radians umod MAX_RADIANS
            val r1 = to.radians umod MAX_RADIANS
            val diff = (r1 - r0 + HALF_RADIANS) % MAX_RADIANS - HALF_RADIANS
            return if (diff < -HALF_RADIANS) Angle(diff + MAX_RADIANS) else Angle(diff)
        }

        fun between(x0: Double, y0: Double, x1: Double, y1: Double): Angle {
            val angle = atan2(y1 - y0, x1 - x0)
            return if (angle < 0) Angle(angle + PI2) else Angle(angle)
        }

        inline fun between(x0: Number, y0: Number, x1: Number, y1: Number): Angle = between(x0.toDouble(), y0.toDouble(), x1.toDouble(), y1.toDouble())

        fun between(p0: IPoint, p1: IPoint): Angle = between(p0.x, p0.y, p1.x, p1.y)
    }
}

fun cos(angle: Angle): Double = kotlin.math.cos(angle.radians)
fun sin(angle: Angle): Double = kotlin.math.sin(angle.radians)
fun tan(angle: Angle): Double = kotlin.math.tan(angle.radians)

val Angle.degrees get() = Angle.radiansToDegrees(radians)

val Angle.absoluteValue: Angle get() = Angle.fromRadians(radians.absoluteValue)
fun Angle.shortDistanceTo(other: Angle): Angle = Angle.shortDistanceTo(this, other)

inline operator fun Angle.times(scale: Number): Angle = Angle(this.radians * scale.toDouble())
inline operator fun Angle.div(scale: Number): Angle = Angle(this.radians / scale.toDouble())
inline operator fun Angle.plus(other: Angle): Angle = Angle(this.radians + other.radians)
inline operator fun Angle.minus(other: Angle): Angle = shortDistanceTo(other)
inline operator fun Angle.unaryMinus(): Angle = Angle(-radians)
inline operator fun Angle.unaryPlus(): Angle = Angle(+radians)

inline val Number.degrees get() = Angle.fromDegrees(this)
inline val Number.radians get() = Angle.fromRadians(this)
val Angle.normalized get() = Angle(radians umod Angle.MAX_RADIANS)
