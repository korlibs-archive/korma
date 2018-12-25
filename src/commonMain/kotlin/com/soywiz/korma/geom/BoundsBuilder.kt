package com.soywiz.korma.geom

class BoundsBuilder {
    val tempRect = Rectangle()

    private var xmin = Float.MAX_VALUE
    private var xmax = Float.MIN_VALUE
    private var ymin = Float.MAX_VALUE
    private var ymax = Float.MIN_VALUE

    fun reset() {
        xmin = Float.MAX_VALUE
        xmax = Float.MIN_VALUE
        ymin = Float.MAX_VALUE
        ymax = Float.MIN_VALUE
    }

    fun add(x: Float, y: Float) = this.apply {
        xmin = kotlin.math.min(xmin, x)
        xmax = kotlin.math.max(xmax, x)
        ymin = kotlin.math.min(ymin, y)
        ymax = kotlin.math.max(ymax, y)
        //println("add($x, $y) -> ($xmin,$ymin)-($xmax,$ymax)")
    }

    fun getBounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(xmin, ymin, xmax, ymax)
}

inline fun BoundsBuilder.add(x: Number, y: Number) = add(x.toFloat(), y.toFloat())

fun BoundsBuilder.add(p: IPoint) = add(p.x, p.y)

fun BoundsBuilder.add(ps: Iterable<IPoint>) = this.apply { for (p in ps) add(p) }

fun BoundsBuilder.add(ps: IPointArrayList) {
    for (n in 0 until ps.size) add(ps.getX(n), ps.getY(n))
}

fun BoundsBuilder.add(rect: Rectangle) = this.apply {
    if (rect.isNotEmpty) {
        add(rect.left, rect.top)
        add(rect.right, rect.bottom)
    }
}

