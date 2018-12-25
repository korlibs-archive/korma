package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.bezier.Bezier
import com.soywiz.korma.geom.bezier.SegmentEmitter

object HorizontalLine {
    fun intersectionsWithLine(
        ax: Float, ay: Float,
        bx0: Float, by0: Float, bx1: Float, by1: Float
    ): Int {
        return if (((by1 > ay) != (by0 > ay)) && (ax < (bx0 - bx1) * (ay - by1) / (by0 - by1) + bx1)) 1 else 0
    }

    fun interesectionsWithQuadBezier(
        ax: Float, ay: Float,
        bx0: Float, by0: Float, bx1: Float, by1: Float, bx2: Float, by2: Float,
        t0: Point = Point(), t1: Point = Point()
    ): Int {
        var count = 0
        SegmentEmitter.emit(4, curveGen = { p, t ->
            Bezier.quadCalc(bx0, by0, bx1, by1, bx2, by2, t, p)
        }, gen = { p0, p1 ->
            count += intersectionsWithLine(ax, ay, p0.x, p0.y, p1.x, p1.y)
        }, p1 = t0, p2 = t1)
        return count
    }

    fun intersectionsWithCubicBezier(
        ax: Float, ay: Float,
        bx0: Float, by0: Float, bx1: Float, by1: Float, bx2: Float, by2: Float, bx3: Float, by3: Float,
        t0: Point = Point(), t1: Point = Point()
    ): Int {
        //return intersectsH0LineLine(ax, ay, bx0, by0, bx3, by3)
        var count = 0
        SegmentEmitter.emit(4, curveGen = { p, t ->
            Bezier.cubicCalc(bx0, by0, bx1, by1, bx2, by2, bx3, by3, t, p)
        }, gen = { p0, p1 ->
            count += intersectionsWithLine(ax, ay, p0.x, p0.y, p1.x, p1.y)
        }, p1 = t0, p2 = t1)
        return count
    }
}
