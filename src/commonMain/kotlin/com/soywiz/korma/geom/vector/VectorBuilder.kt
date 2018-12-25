package com.soywiz.korma.geom.vector

import com.soywiz.korma.geom.*
import kotlin.math.*

interface VectorBuilder {
    val totalPoints: Int
    val lastX: Double
    val lastY: Double
    fun moveTo(x: Double, y: Double)
    fun lineTo(x: Double, y: Double)
    fun quadTo(controlX: Double, controlY: Double, anchorX: Double, anchorY: Double)
    fun cubicTo(cx1: Double, cy1: Double, cx2: Double, cy2: Double, ax: Double, ay: Double)
    fun close()
}

fun VectorBuilder.isEmpty() = totalPoints == 0
fun VectorBuilder.isNotEmpty() = totalPoints != 0

//fun arcTo(b: Point2d, a: Point2d, c: Point2d, r: Double) {
fun VectorBuilder.arcTo(ax: Double, ay: Double, cx: Double, cy: Double, r: Double) {
    if (isEmpty()) moveTo(ax, ay)
    val bx = lastX
    val by = lastY
    val b = IPoint(bx, by)
    val a = IPoint(ax, ay)
    val c = IPoint(cx, cy)
    val AB = b - a
    val AC = c - a
    val angle = Point.angle(AB, AC) * 0.5
    val x = r * sin((PI / 2.0) - angle) / sin(angle)
    val A = a + AB.unit * x
    val B = a + AC.unit * x
    lineTo(A.x, A.y)
    quadTo(a.x, a.y, B.x, B.y)
}

fun VectorBuilder.rect(x: Double, y: Double, width: Double, height: Double) {
    moveTo(x, y)
    lineTo(x + width, y)
    lineTo(x + width, y + height)
    lineTo(x, y + height)
    close()
}

fun VectorBuilder.rectHole(x: Double, y: Double, width: Double, height: Double) {
    moveTo(x, y)
    lineTo(x, y + height)
    lineTo(x + width, y + height)
    lineTo(x + width, y)
    close()
}

fun VectorBuilder.roundRect(x: Double, y: Double, w: Double, h: Double, rx: Double, ry: Double = rx) {
    if (rx == 0.0 && ry == 0.0) {
        rect(x, y, w, h)
    } else {
        val r = if (w < 2 * rx) w / 2.0 else if (h < 2 * rx) h / 2.0 else rx
        this.moveTo(x + r, y)
        this.arcTo(x + w, y, x + w, y + h, r)
        this.arcTo(x + w, y + h, x, y + h, r)
        this.arcTo(x, y + h, x, y, r)
        this.arcTo(x, y, x + w, y, r)
    }
}

fun VectorBuilder.arc(x: Double, y: Double, r: Double, start: Double, end: Double) {
    // http://hansmuller-flex.blogspot.com.es/2011/04/approximating-circular-arc-with-cubic.html
    val EPSILON = 0.00001
    val PI_TWO = PI * 2
    val PI_OVER_TWO = PI / 2.0

    val startAngle = start % PI_TWO
    val endAngle = end % PI_TWO
    var remainingAngle = kotlin.math.min(PI_TWO, abs(endAngle - startAngle))
    if (remainingAngle == 0.0 && start != end) remainingAngle = PI_TWO
    val sgn = if (startAngle < endAngle) 1 else -1
    var a1 = startAngle
    val p1 = Point()
    val p2 = Point()
    val p3 = Point()
    val p4 = Point()
    var index = 0
    while (remainingAngle > EPSILON) {
        val a2 = a1 + sgn * kotlin.math.min(remainingAngle, PI_OVER_TWO)

        val k = 0.5522847498
        val a = (a2 - a1) / 2.0
        val x4 = r * cos(a)
        val y4 = r * sin(a)
        val x1 = x4
        val y1 = -y4
        val f = k * tan(a)
        val x2 = x1 + f * y4
        val y2 = y1 + f * x4
        val x3 = x2
        val y3 = -y2
        val ar = a + a1
        val cos_ar = cos(ar)
        val sin_ar = sin(ar)
        p1.setTo(x + r * cos(a1), y + r * sin(a1))
        p2.setTo(x + x2 * cos_ar - y2 * sin_ar, y + x2 * sin_ar + y2 * cos_ar)
        p3.setTo(x + x3 * cos_ar - y3 * sin_ar, y + x3 * sin_ar + y3 * cos_ar)
        p4.setTo(x + r * cos(a2), y + r * sin(a2))

        if (index == 0) moveTo(p1.x, p1.y)
        cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)

        index++
        remainingAngle -= abs(a2 - a1)
        a1 = a2
    }
    if (startAngle == endAngle && index != 0) {
        close()
    }
}

fun VectorBuilder.circle(x: Double, y: Double, radius: Double) = arc(x, y, radius, 0.0, PI * 2.0)

fun VectorBuilder.ellipse(x: Double, y: Double, rw: Double, rh: Double) {
    val k = .5522848
    val ox = (rw / 2) * k
    val oy = (rh / 2) * k
    val xe = x + rw
    val ye = y + rh
    val xm = x + rw / 2
    val ym = y + rh / 2
    moveTo(x, ym)
    cubicTo(x, ym - oy, xm - ox, y, xm, y)
    cubicTo(xm + ox, y, xe, ym - oy, xe, ym)
    cubicTo(xe, ym + oy, xm + ox, ye, xm, ye)
    cubicTo(xm - ox, ye, x, ym + oy, x, ym)
}

inline fun VectorBuilder.moveTo(x: Number, y: Number) = moveTo(x.toDouble(), y.toDouble())
inline fun VectorBuilder.lineTo(x: Number, y: Number) = lineTo(x.toDouble(), y.toDouble())
inline fun VectorBuilder.quadTo(controlX: Number, controlY: Number, anchorX: Number, anchorY: Number) = quadTo(controlX.toDouble(), controlY.toDouble(), anchorX.toDouble(), anchorY.toDouble())
inline fun VectorBuilder.cubicTo(cx1: Number, cy1: Number, cx2: Number, cy2: Number, ax: Number, ay: Number) = cubicTo(cx1.toDouble(), cy1.toDouble(), cx2.toDouble(), cy2.toDouble(), ax.toDouble(), ay.toDouble())

inline fun VectorBuilder.moveToH(x: Number) = moveTo(x, lastY)
inline fun VectorBuilder.rMoveToH(x: Number) = moveTo(lastX + x.toDouble(), lastY)

inline fun VectorBuilder.moveToV(y: Number) = moveTo(lastX, y)
inline fun VectorBuilder.rMoveToV(y: Number) = moveTo(lastX, lastY + y.toDouble())

inline fun VectorBuilder.lineToH(x: Number) = lineTo(x, lastY)
inline fun VectorBuilder.rLineToH(x: Number) = lineTo(lastX + x.toDouble(), lastY)

inline fun VectorBuilder.lineToV(y: Number) = lineTo(lastX, y)
inline fun VectorBuilder.rLineToV(y: Number) = lineTo(lastX, lastY + y.toDouble())

inline fun VectorBuilder.rMoveTo(x: Number, y: Number) = moveTo(this.lastX + x.toDouble(), this.lastY + y.toDouble())
inline fun VectorBuilder.rLineTo(x: Number, y: Number) = lineTo(this.lastX + x.toDouble(), this.lastY + y.toDouble())

inline fun VectorBuilder.rQuadTo(cx: Number, cy: Number, ax: Number, ay: Number) = quadTo(this.lastX + cx.toDouble(), this.lastY + cy.toDouble(), this.lastX + ax.toDouble(), this.lastY + ay.toDouble())

inline fun VectorBuilder.rCubicTo(cx1: Number, cy1: Number, cx2: Number, cy2: Number, ax: Number, ay: Number) = cubicTo(
    this.lastX + cx1.toDouble(), this.lastY + cy1.toDouble(),
    this.lastX + cx2.toDouble(), this.lastY + cy2.toDouble(),
    this.lastX + ax.toDouble(), this.lastY + ay.toDouble()
)

inline fun VectorBuilder.arcTo(ax: Number, ay: Number, cx: Number, cy: Number, r: Number) = arcTo(ax.toDouble(), ay.toDouble(), cx.toDouble(), cy.toDouble(), r.toDouble())
inline fun VectorBuilder.rect(x: Number, y: Number, width: Number, height: Number) = rect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
inline fun VectorBuilder.rectHole(x: Number, y: Number, width: Number, height: Number) = rectHole(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
inline fun VectorBuilder.roundRect(x: Number, y: Number, w: Number, h: Number, rx: Number, ry: Number = rx) = roundRect(x.toDouble(), y.toDouble(), w.toDouble(), h.toDouble(), rx.toDouble(), ry.toDouble())
inline fun VectorBuilder.arc(x: Number, y: Number, r: Number, start: Number, end: Number) = arc(x.toDouble(), y.toDouble(), r.toDouble(), start.toDouble(), end.toDouble())
inline fun VectorBuilder.circle(x: Number, y: Number, radius: Number) = circle(x.toDouble(), y.toDouble(), radius.toDouble())
inline fun VectorBuilder.ellipse(x: Number, y: Number, rw: Number, rh: Number) = ellipse(x.toDouble(), y.toDouble(), rw.toDouble(), rh.toDouble())
