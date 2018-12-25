package com.soywiz.korma.geom.shape.ops

import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.geom.shape.ops.internal.*
import com.soywiz.korma.geom.vector.*

infix fun Shape2d.collidesWith(other: Shape2d): Boolean =
    this.clipperOp(other, Clipper.ClipType.INTERSECTION) != Shape2d.Empty

infix fun Shape2d.intersection(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.INTERSECTION)
infix fun Shape2d.union(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.UNION)
infix fun Shape2d.xor(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.XOR)
infix fun Shape2d.difference(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.DIFFERENCE)

operator fun Shape2d.plus(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.UNION)
operator fun Shape2d.minus(other: Shape2d): Shape2d = this.clipperOp(other, Clipper.ClipType.DIFFERENCE)

fun Shape2d.extend(size: Float, cap: VectorPath.LineCap = VectorPath.LineCap.ROUND): Shape2d {
    val clipper = ClipperOffset()
    val solution = Paths()
    clipper.addPaths(
        this.paths.toClipperPaths(), Clipper.JoinType.MITER,
        if (this.closed) Clipper.EndType.CLOSED_POLYGON else cap.toClipper()
    )
    clipper.execute(solution, size)
    return solution.toShape2d()
}

fun Shape2d.extendLine(size: Float, join: VectorPath.LineJoin = VectorPath.LineJoin.SQUARE, cap: VectorPath.LineCap = VectorPath.LineCap.SQUARE): Shape2d {
    val clipper = ClipperOffset()
    val solution = Paths()
    clipper.addPaths(
        this.paths.toClipperPaths(), join.toClipper(),
        if (this.closed) Clipper.EndType.CLOSED_LINE else cap.toClipper()
    )
    clipper.execute(solution, size)
    return solution.toShape2d()
}

//inline fun Shape2d.extend(size: Number, cap: VectorPath.LineCap = VectorPath.LineCap.ROUND): Shape2d = extend(size.toFloat(), cap)
//inline fun Shape2d.extendLine(size: Number, join: VectorPath.LineJoin = VectorPath.LineJoin.SQUARE, cap: VectorPath.LineCap) = extendLine(size.toFloat(), join, cap)

fun VectorPath.toShape2d(): Shape2d = this.toClipperPaths().toShape2d()
