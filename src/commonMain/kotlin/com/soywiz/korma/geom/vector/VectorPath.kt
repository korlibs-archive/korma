package com.soywiz.korma.geom.vector

import com.soywiz.kds.*
import com.soywiz.korma.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.shape.*

open class VectorPath(
    val commands: IntArrayList = IntArrayList(),
    val data: DoubleArrayList = DoubleArrayList(),
    val winding: Winding = Winding.EVEN_ODD
) : VectorBuilder {
    open fun clone(): VectorPath = VectorPath(IntArrayList(commands), DoubleArrayList(data), winding)

    interface Visitor {
        fun close()
        fun moveTo(x: Double, y: Double)
        fun lineTo(x: Double, y: Double)
        fun quadTo(cx: Double, cy: Double, ax: Double, ay: Double)
        fun cubicTo(cx1: Double, cy1: Double, cx2: Double, cy2: Double, ax: Double, ay: Double)
    }

    inline fun visitCmds(
        moveTo: (x: Double, y: Double) -> Unit,
        lineTo: (x: Double, y: Double) -> Unit,
        quadTo: (x1: Double, y1: Double, x2: Double, y2: Double) -> Unit,
        cubicTo: (x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) -> Unit,
        close: () -> Unit
    ) {
        var n = 0
        for (cmd in commands) {
            when (cmd) {
                Command.MOVE_TO -> {
                    val x = data[n++]
                    val y = data[n++]
                    moveTo(x, y)
                }
                Command.LINE_TO -> {
                    val x = data[n++]
                    val y = data[n++]
                    lineTo(x, y)
                }
                Command.QUAD_TO -> {
                    val x1 = data[n++]
                    val y1 = data[n++]
                    val x2 = data[n++]
                    val y2 = data[n++]
                    quadTo(x1, y1, x2, y2)
                }
                Command.CUBIC_TO -> {
                    val x1 = data[n++]
                    val y1 = data[n++]
                    val x2 = data[n++]
                    val y2 = data[n++]
                    val x3 = data[n++]
                    val y3 = data[n++]
                    cubicTo(x1, y1, x2, y2, x3, y3)
                }
                Command.CLOSE -> {
                    close()
                }
            }
        }
    }

    inline fun visitEdges(
        line: (x0: Double, y0: Double, x1: Double, y1: Double) -> Unit,
        quad: (x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double) -> Unit,
        cubic: (x0: Double, y0: Double, x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double) -> Unit,
        close: () -> Unit
    ) {
        var mx = 0.0
        var my = 0.0
        var lx = 0.0
        var ly = 0.0
        visitCmds(
            moveTo = { x, y ->
                mx = x; my = y
                lx = x; ly = y
            },
            lineTo = { x, y ->
                line(lx, ly, x, y)
                lx = x; ly = y
            },
            quadTo = { x1, y1, x2, y2 ->
                quad(lx, ly, x1, y1, x2, y2)
                lx = x2; ly = y2
            },
            cubicTo = { x1, y1, x2, y2, x3, y3 ->
                cubic(lx, ly, x1, y1, x2, y2, x3, y3)
                lx = x3; ly = y3
            },
            close = {
                if ((lx != mx) || (ly != my)) {
                    line(lx, ly, mx, my)
                }
                close()
            }
        )
    }

    fun visit(visitor: Visitor) {
        visitCmds(
            moveTo = visitor::moveTo,
            lineTo = visitor::lineTo,
            quadTo = visitor::quadTo,
            cubicTo = visitor::cubicTo,
            close = visitor::close
        )
    }

    fun clear() {
        commands.clear()
        data.clear()
    }

    fun setFrom(other: VectorPath) {
        clear()
        appendFrom(other)
    }

    fun appendFrom(other: VectorPath) {
        this.commands.add(other.commands)
        this.data.add(other.data)
        this.lastX = other.lastX
        this.lastY = other.lastY
    }

    override var lastX = 0.0
    override var lastY = 0.0

    override fun moveTo(x: Double, y: Double) {
        commands += Command.MOVE_TO
        data += x
        data += y
        lastX = x
        lastY = y
    }

    override fun lineTo(x: Double, y: Double) {
        ensureMoveTo(x, y)
        commands += Command.LINE_TO
        data += x
        data += y
        lastX = x
        lastY = y
    }

    override fun quadTo(controlX: Double, controlY: Double, anchorX: Double, anchorY: Double) {
        ensureMoveTo(controlX, controlY)
        commands += Command.QUAD_TO
        data += controlX
        data += controlY
        data += anchorX
        data += anchorY
        lastX = anchorX
        lastY = anchorY
    }

    override fun cubicTo(cx1: Double, cy1: Double, cx2: Double, cy2: Double, ax: Double, ay: Double) {
        ensureMoveTo(cx1, cy1)
        commands += Command.CUBIC_TO
        data += cx1
        data += cy1
        data += cx2
        data += cy2
        data += ax
        data += ay
        lastX = ax
        lastY = ay
    }

    override fun close() {
        commands += Command.CLOSE
    }

    override val totalPoints: Int get() = data.size / 2

    private fun ensureMoveTo(x: Double, y: Double) {
        if (isEmpty()) moveTo(x, y)
    }

    private val bezierTemp = Bezier.Temp()

    fun appendBounds(bb: BoundsBuilder) {
        var lx = 0.0
        var ly = 0.0

        visitCmds(
            moveTo = { x, y ->
                bb.add(x, y)
                lx = x
                ly = y
            },
            lineTo = { x, y ->
                bb.add(x, y)
                lx = x
                ly = y
            },
            quadTo = { cx, cy, ax, ay ->
                bb.add(Bezier.quadBounds(lx, ly, cx, cy, ax, ay, bb.tempRect))
                lx = ax
                ly = ay
            },
            cubicTo = { cx1, cy1, cx2, cy2, ax, ay ->
                bb.add(Bezier.cubicBounds(lx, ly, cx1, cy1, cx2, cy2, ax, ay, bb.tempRect, bezierTemp))
                lx = ax
                ly = ay
            },
            close = {

            }
        )
    }

    fun getBounds(out: Rectangle = Rectangle(), bb: BoundsBuilder = BoundsBuilder()): Rectangle {
        bb.reset()
        appendBounds(bb)
        return bb.getBounds(out)
    }

    fun getPoints(): List<Point2d> {
        val points = arrayListOf<Point2d>()
        this.visitCmds(
            moveTo = { x, y -> points += Point2d(x, y) },
            lineTo = { x, y -> points += Point2d(x, y) },
            quadTo = { x1, y1, x2, y2 -> points += Point2d(x2, y2) },
            cubicTo = { x1, y1, x2, y2, x3, y3 -> points += Point2d(x3, y3) },
            close = { }
        )
        return points
    }

    private val p1 = MVector2()
    private val p2 = MVector2()

    // http://erich.realtimerendering.com/ptinpoly/
    // http://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon/2922778#2922778
    // https://www.particleincell.com/2013/cubic-line-intersection/
    // I run a semi-infinite ray horizontally (increasing x, fixed y) out from the test point, and count how many edges it crosses.
    // At each crossing, the ray switches between inside and outside. This is called the Jordan curve theorem.
    fun containsPoint(x: Double, y: Double): Boolean {
        val testx = x
        val testy = y

        var intersections = 0

        visitEdges(
            line = { x0, y0, x1, y1 -> intersections += HorizontalLine.intersectionsWithLine(testx, testy, x0, y0, x1, y1) },
            quad = { x0, y0, x1, y1, x2, y2 -> intersections += HorizontalLine.interesectionsWithQuadBezier(testx, testy, x0, y0, x1, y1, x2, y2, p1, p2) },
            cubic = { x0, y0, x1, y1, x2, y2, x3, y3 -> intersections += HorizontalLine.intersectionsWithCubicBezier(testx, testy, x0, y0, x1, y1, x2, y2, x3, y3, p1, p2) },
            close = {}
        )
        return (intersections % 2) != 0
    }

    inline fun containsPoint(x: Number, y: Number): Boolean = containsPoint(x.toDouble(), y.toDouble())

    object Command {
        const val MOVE_TO = 1
        const val LINE_TO = 2
        const val QUAD_TO = 3
        const val CUBIC_TO = 4
        const val CLOSE = 5
    }

    enum class Winding(val str: String) {
        EVEN_ODD("evenOdd"), NON_ZERO("nonZero");
    }

    enum class LineCap { BUTT, SQUARE, ROUND }
    enum class LineJoin { SQUARE, ROUND, MITER }

    fun write(path: VectorPath) {
        this.commands += path.commands
        this.data += path.data
        this.lastX = path.lastX
        this.lastY = path.lastY
    }
}
