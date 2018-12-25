package com.soywiz.korma.geom.bezier

import com.soywiz.korma.geom.*

object SegmentEmitter {
    inline fun emit(
        segments: Int,
        crossinline curveGen: (p: Point, t: Float) -> Point,
        crossinline gen: (p0: Point, p1: Point) -> Unit,
        p1: Point = Point(),
        p2: Point = Point()
    ) {
        val dt = 1f / segments
        for (n in 0 until segments) {
            p1.copyFrom(p2)
            p2.copyFrom(curveGen(p2, dt * n))
            if (n > 1) gen(p1, p2)
        }
    }
}
