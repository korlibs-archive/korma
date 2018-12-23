package com.soywiz.korma.geom.clipper

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.math.*

fun Path.toShape2d(): Shape2d {
    if (this.size == 4) {
        for (n in 0 until 4) {
            val tl = this[(n + 0) % 4]
            val tr = this[(n + 1) % 4]
            val br = this[(n + 2) % 4]
            val bl = this[(n + 3) % 4]

            if ((tl.x == bl.x) && (tr.x == br.x) && (tl.y == tr.y) && (bl.y == br.y)) {
                val xmin = kotlin.math.min(tl.x, tr.x)
                val xmax = kotlin.math.max(tl.x, tr.x)
                val ymin = kotlin.math.min(tl.y, bl.y)
                val ymax = kotlin.math.max(tl.y, bl.y)
                //println("($xmin,$ymin)-($xmax-$ymax) : $tl,$tr,$br,$bl")
                return Shape2d.Rectangle(xmin, ymin, xmax - xmin, ymax - ymin)
            }
        }
    }
    // @TODO: Try to detect rectangle
    return Shape2d.Polygon(this)
}

fun Paths.toShape2d(): Shape2d {
    return when (size) {
        0 -> Shape2d.Empty
        1 -> first().toShape2d()
        else -> Shape2d.Complex(this.map(Path::toShape2d))
    }
}

fun Shape2d.clipperOp(other: Shape2d, op: Clipper.ClipType): Shape2d {
    val clipper = DefaultClipper()
    val solution = Paths()
    clipper.addPaths(this.paths, Clipper.PolyType.CLIP, other.closed)
    clipper.addPaths(other.paths, Clipper.PolyType.SUBJECT, other.closed)
    clipper.execute(op, solution)
    return solution.toShape2d()
}

fun VectorPath.toPaths(): Paths {
    return Paths(toPaths2().map { Path(it) })
}
