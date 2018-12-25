package com.soywiz.korma.geom

class BoundsBuilder {
    val tempRect = Rectangle()

    private var xmin = Double.MAX_VALUE
    private var xmax = Double.MIN_VALUE
    private var ymin = Double.MAX_VALUE
    private var ymax = Double.MIN_VALUE

    fun reset() {
        xmin = Double.MAX_VALUE
        xmax = Double.MIN_VALUE
        ymin = Double.MAX_VALUE
        ymax = Double.MIN_VALUE
    }

    fun add(x: Double, y: Double): BoundsBuilder {
        xmin = kotlin.math.min(xmin, x)
        xmax = kotlin.math.max(xmax, x)
        ymin = kotlin.math.min(ymin, y)
        ymax = kotlin.math.max(ymax, y)
        //println("add($x, $y) -> ($xmin,$ymin)-($xmax,$ymax)")
        return this
    }

    fun getBounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(xmin, ymin, xmax, ymax)
}

inline fun BoundsBuilder.add(x: Number, y: Number) = add(x.toDouble(), y.toDouble())
fun BoundsBuilder.add(p: IPoint) = add(p.x, p.y)
fun BoundsBuilder.add(ps: Iterable<IPoint>) = this.apply { for (p in ps) add(p) }
fun BoundsBuilder.add(ps: IPointArrayList) = run { for (n in 0 until ps.size) add(ps.getX(n), ps.getY(n)) }
fun BoundsBuilder.add(rect: Rectangle) = this.apply {
    if (rect.isNotEmpty) {
        add(rect.left, rect.top)
        add(rect.right, rect.bottom)
    }
}
