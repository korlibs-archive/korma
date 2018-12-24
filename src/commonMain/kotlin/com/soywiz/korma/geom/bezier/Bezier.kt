package com.soywiz.korma.geom.bezier

import com.soywiz.korma.*
import com.soywiz.korma.geom.*
import kotlin.math.*

//(x0,y0) is start point; (x1,y1),(x2,y2) is control points; (x3,y3) is end point.
interface Bezier {
    fun getBounds(target: Rectangle = Rectangle()): Rectangle
    fun calc(t: Double, target: MVector2 = MVector2()): MVector2

    class Quad(val p0: Point2d, val p1: Point2d, val p2: Point2d) : Bezier {
        override fun getBounds(target: Rectangle): Rectangle = quadBounds(p0.x.toDouble(), p0.y.toDouble(), p1.x.toDouble(), p1.y.toDouble(), p2.x.toDouble(), p2.y.toDouble(), target)
        override fun calc(t: Double, target: MVector2): MVector2 = quadCalc(p0.x.toDouble(), p0.y.toDouble(), p1.x.toDouble(), p1.y.toDouble(), p2.x.toDouble(), p2.y.toDouble(), t, target)

        // http://fontforge.github.io/bezier.html
        fun toCubic(): Cubic = Cubic(p0, p0 + (p1 - p0) * (2.0 / 3.0), p2 + (p1 - p2) * (2.0 / 3.0), p2)
    }

    class Cubic(val p0: Point2d, val p1: Point2d, val p2: Point2d, val p3: Point2d) : Bezier {
        private val temp = Temp()

        override fun getBounds(target: Rectangle): Rectangle = cubicBounds(p0.x.toDouble(), p0.y.toDouble(), p1.x.toDouble(), p1.y.toDouble(), p2.x.toDouble(), p2.y.toDouble(), p3.x.toDouble(), p3.y.toDouble(), target, temp)
        override fun calc(t: Double, target: MVector2): MVector2 = cubicCalc(p0.x.toDouble(), p0.y.toDouble(), p1.x.toDouble(), p1.y.toDouble(), p2.x.toDouble(), p2.y.toDouble(), p3.x.toDouble(), p3.y.toDouble(), t, target)
    }

    class Temp {
        val tvalues = DoubleArray(6)
        val xvalues = DoubleArray(8)
        val yvalues = DoubleArray(8)
    }

    companion object {
        operator fun invoke(p0: Point2d, p1: Point2d, p2: Point2d): Bezier.Quad = Bezier.Quad(p0, p1, p2)
        operator fun invoke(p0: Point2d, p1: Point2d, p2: Point2d, p3: Point2d): Bezier.Cubic =
            Bezier.Cubic(p0, p1, p2, p3)

        // http://fontforge.github.io/bezier.html
        //Any quadratic spline can be expressed as a cubic (where the cubic term is zero). The end points of the cubic will be the same as the quadratic's.
        //CP0 = QP0
        //CP3 = QP2
        //The two control points for the cubic are:
        //CP1 = QP0 + 2/3 *(QP1-QP0)
        //CP2 = QP2 + 2/3 *(QP1-QP2)
        inline fun <T> quadToCubic(
            x0: Double, y0: Double, xc: Double, yc: Double, x1: Double, y1: Double,
            bezier: (x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) -> T
        ): T {
            return bezier(
                x0, y0,
                x0 + 2 / 3 * (xc - x0), y0 + 2 / 3 * (yc - y0),
                x1 + 2 / 3 * (xc - x1), y1 + 2 / 3 * (yc - y1),
                x1, y1
            )
        }

        fun quadBounds(
            x0: Double, y0: Double,
            xc: Double, yc: Double,
            x1: Double, y1: Double,
            target: Rectangle = Rectangle(),
            temp: Temp = Temp()
            // @TODO: Make an optimized version!
        ): Rectangle = quadToCubic(x0, y0, xc, yc, x1, y1) { aX, aY, bX, bY, cX, cY, dX, dY ->
            cubicBounds(aX, aY, bX, bY, cX, cY, dX, dY, target, temp)
        }

        inline fun <T> quadCalc(
            x0: Double, y0: Double,
            xc: Double, yc: Double,
            x1: Double, y1: Double,
            t: Double,
            emit: (x: Double, y: Double) -> T
        ): T {
            //return quadToCubic(x0, y0, xc, yc, x1, y1) { x0, y0, x1, y1, x2, y2, x3, y3 -> cubicCalc(x0, y0, x1, y1, x2, y2, x3, y3, t, emit) }
            val t1 = (1 - t)
            val a = t1 * t1
            val c = t * t
            val b = 2 * t1 * t
            return emit(
                a * x0 + b * xc + c * x1,
                a * y0 + b * yc + c * y1
            )
        }

        fun quadCalc(
            x0: Double, y0: Double,
            xc: Double, yc: Double,
            x1: Double, y1: Double,
            t: Double,
            target: MVector2 = MVector2()
        ): MVector2 = quadCalc(x0, y0, xc, yc, x1, y1, t) { x, y -> target.setTo(x, y) }

        fun cubicBounds(
            x0: Double, y0: Double, x1: Double, y1: Double,
            x2: Double, y2: Double, x3: Double, y3: Double,
            target: Rectangle = Rectangle(),
            temp: Temp = Temp()
        ): Rectangle {
            var j = 0
            var a: Double
            var b: Double
            var c: Double
            var b2ac: Double
            var sqrtb2ac: Double
            for (i in 0 until 2) {
                if (i == 0) {
                    b = 6 * x0 - 12 * x1 + 6 * x2
                    a = -3 * x0 + 9 * x1 - 9 * x2 + 3 * x3
                    c = 3 * x1 - 3 * x0
                } else {
                    b = 6 * y0 - 12 * y1 + 6 * y2
                    a = -3 * y0 + 9 * y1 - 9 * y2 + 3 * y3
                    c = 3 * y1 - 3 * y0
                }
                if (abs(a) < 1e-12) {
                    if (abs(b) >= 1e-12) {
                        val t = -c / b
                        if (0 < t && t < 1) temp.tvalues[j++] = t
                    }
                } else {
                    b2ac = b * b - 4 * c * a
                    if (b2ac < 0) continue
                    sqrtb2ac = sqrt(b2ac)
                    val t1 = (-b + sqrtb2ac) / (2 * a)
                    if (0 < t1 && t1 < 1) temp.tvalues[j++] = t1
                    val t2 = (-b - sqrtb2ac) / (2 * a)
                    if (0 < t2 && t2 < 1) temp.tvalues[j++] = t2
                }
            }

            while (j-- > 0) {
                val t = temp.tvalues[j]
                val mt = 1 - t
                temp.xvalues[j] = (mt * mt * mt * x0) + (3 * mt * mt * t * x1) + (3 * mt * t * t * x2) +
                    (t * t * t * x3)
                temp.yvalues[j] = (mt * mt * mt * y0) + (3 * mt * mt * t * y1) + (3 * mt * t * t * y2) +
                    (t * t * t * y3)
            }

            temp.xvalues[temp.tvalues.size + 0] = x0
            temp.xvalues[temp.tvalues.size + 1] = x3
            temp.yvalues[temp.tvalues.size + 0] = y0
            temp.yvalues[temp.tvalues.size + 1] = y3

            return target.setBounds(
                temp.xvalues.min() ?: 0.0,
                temp.yvalues.min() ?: 0.0,
                temp.xvalues.max() ?: 0.0,
                temp.yvalues.max() ?: 0.0
            )
        }

        inline fun <T> cubicCalc(
            x0: Double, y0: Double, x1: Double, y1: Double,
            x2: Double, y2: Double, x3: Double, y3: Double,
            t: Double,
            emit: (x: Double, y: Double) -> T
        ): T {
            val cx = 3.0 * (x1 - x0)
            val bx = 3.0 * (x2 - x1) - cx
            val ax = x3 - x0 - cx - bx

            val cy = 3.0 * (y1 - y0)
            val by = 3.0 * (y2 - y1) - cy
            val ay = y3 - y0 - cy - by

            val tSquared = t * t
            val tCubed = tSquared * t

            return emit(
                ax * tCubed + bx * tSquared + cx * t + x0,
                ay * tCubed + by * tSquared + cy * t + y0
            )
        }

        // http://stackoverflow.com/questions/7348009/y-coordinate-for-a-given-x-cubic-bezier
        fun cubicCalc(
            x0: Double, y0: Double, x1: Double, y1: Double,
            x2: Double, y2: Double, x3: Double, y3: Double,
            t: Double, target: MVector2 = MVector2()
        ): MVector2 = cubicCalc(x0, y0, x1, y1, x2, y2, x3, y3, t) { x, y -> target.setTo(x, y) }
    }
}

fun Bezier.length(steps: Int = 100, temp: MVector2 = MVector2()): Double {
    val dt = 1.0 / steps.toDouble()
    var oldX = 0.0
    var oldY = 0.0
    var length = 0.0
    for (n in 0..steps) {
        calc(dt * n, temp)
        if (n != 0) {
            length += hypot(oldX - temp.x, oldY - temp.y)
        }
        oldX = temp.x.toDouble()
        oldY = temp.y.toDouble()
    }
    return length
}
