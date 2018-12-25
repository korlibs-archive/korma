package com.soywiz.korma.geom.vector

import com.soywiz.kds.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.shape.*

open class VectorPath(
    val commands: IntArrayList = IntArrayList(),
    val data: FloatArrayList = FloatArrayList(),
    val winding: Winding = Winding.EVEN_ODD
) : VectorBuilder {
    open fun clone(): VectorPath = VectorPath(IntArrayList(commands), FloatArrayList(data), winding)

    interface Visitor {
        fun close()
        fun moveTo(x: Float, y: Float)
        fun lineTo(x: Float, y: Float)
        fun quadTo(cx: Float, cy: Float, ax: Float, ay: Float)
        fun cubicTo(cx1: Float, cy1: Float, cx2: Float, cy2: Float, ax: Float, ay: Float)
    }

    inline fun visitCmds(
        moveTo: (x: Float, y: Float) -> Unit,
        lineTo: (x: Float, y: Float) -> Unit,
        quadTo: (x1: Float, y1: Float, x2: Float, y2: Float) -> Unit,
        cubicTo: (x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) -> Unit,
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
        line: (x0: Float, y0: Float, x1: Float, y1: Float) -> Unit,
        quad: (x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float) -> Unit,
        cubic: (x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) -> Unit,
        close: () -> Unit
    ) {
        var mx = 0f
        var my = 0f
        var lx = 0f
        var ly = 0f
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

    override var lastX = 0f
    override var lastY = 0f

    override fun moveTo(x: Float, y: Float) {
        commands += Command.MOVE_TO
        data += x
        data += y
        lastX = x
        lastY = y
    }

    override fun lineTo(x: Float, y: Float) {
        ensureMoveTo(x, y)
        commands += Command.LINE_TO
        data += x
        data += y
        lastX = x
        lastY = y
    }

    override fun quadTo(cx: Float, cy: Float, ax: Float, ay: Float) {
        ensureMoveTo(cx, cy)
        commands += Command.QUAD_TO
        data += cx
        data += cy
        data += ax
        data += ay
        lastX = ax
        lastY = ay
    }

    override fun cubicTo(cx1: Float, cy1: Float, cx2: Float, cy2: Float, ax: Float, ay: Float) {
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

    private fun ensureMoveTo(x: Float, y: Float) {
        if (isEmpty()) moveTo(x, y)
    }

    @PublishedApi
    internal val bezierTemp = Bezier.Temp()

    fun getBounds(out: Rectangle = Rectangle(), bb: BoundsBuilder = BoundsBuilder()): Rectangle {
        bb.reset()
        bb.add(this)
        return bb.getBounds(out)
    }

    fun getPoints(): List<IPoint> {
        val points = arrayListOf<IPoint>()
        this.visitCmds(
            moveTo = { x, y -> points += IPoint(x, y) },
            lineTo = { x, y -> points += IPoint(x, y) },
            quadTo = { x1, y1, x2, y2 -> points += IPoint(x2, y2) },
            cubicTo = { x1, y1, x2, y2, x3, y3 -> points += IPoint(x3, y3) },
            close = { }
        )
        return points
    }

    private val p1 = Point()
    private val p2 = Point()

    // http://erich.realtimerendering.com/ptinpoly/
    // http://stackoverflow.com/questions/217578/how-can-i-determine-whether-a-2d-point-is-within-a-polygon/2922778#2922778
    // https://www.particleincell.com/2013/cubic-line-intersection/
    // I run a semi-infinite ray horizontally (increasing x, fixed y) out from the test point, and count how many edges it crosses.
    // At each crossing, the ray switches between inside and outside. This is called the Jordan curve theorem.
    fun containsPoint(x: Float, y: Float): Boolean = (numberOfIntersections(x, y) % 2) != 0

    fun numberOfIntersections(x: Float, y: Float): Int {
        val testx = x
        val testy = y

        var intersections = 0

        visitEdges(
            line = { x0, y0, x1, y1 -> intersections += HorizontalLine.intersectionsWithLine(testx, testy, x0, y0, x1, y1) },
            quad = { x0, y0, x1, y1, x2, y2 -> intersections += HorizontalLine.interesectionsWithQuadBezier(testx, testy, x0, y0, x1, y1, x2, y2, p1, p2) },
            cubic = { x0, y0, x1, y1, x2, y2, x3, y3 -> intersections += HorizontalLine.intersectionsWithCubicBezier(testx, testy, x0, y0, x1, y1, x2, y2, x3, y3, p1, p2) },
            close = {}
        )
        return intersections
    }

    class Stats {
        val stats = IntArray(5)
        val moveTo get() = stats[Command.MOVE_TO]
        val lineTo get() = stats[Command.LINE_TO]
        val quadTo get() = stats[Command.QUAD_TO]
        val cubicTo get() = stats[Command.CUBIC_TO]
        val close get() = stats[Command.CLOSE]
        fun reset() {
            for (n in stats.indices) stats[n] = 0
        }
        override fun toString(): String = "Stats(moveTo=$moveTo, lineTo=$lineTo, quadTo=$quadTo, cubicTo=$cubicTo, close=$close)"
    }

    fun readStats(out: Stats = Stats()): Stats {
        out.reset()
        for (cmd in commands) out.stats[cmd]++
        return out
    }

    object Command {
        const val MOVE_TO = 0
        const val LINE_TO = 1
        const val QUAD_TO = 2
        const val CUBIC_TO = 3
        const val CLOSE = 4
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

inline fun VectorPath.containsPoint(x: Number, y: Number): Boolean = containsPoint(x.toFloat(), y.toFloat())
inline fun VectorPath.numberOfIntersections(x: Number, y: Number): Int = numberOfIntersections(x.toFloat(), y.toFloat())

fun BoundsBuilder.add(path: VectorPath) {
    val bb = this
    var lx = 0f
    var ly = 0f

    path.visitCmds(
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
            bb.add(Bezier.cubicBounds(lx, ly, cx1, cy1, cx2, cy2, ax, ay, bb.tempRect, path.bezierTemp))
            lx = ax
            ly = ay
        },
        close = {

        }
    )
}
