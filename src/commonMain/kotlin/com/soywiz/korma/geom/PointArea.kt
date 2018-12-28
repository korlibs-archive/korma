package com.soywiz.korma.geom

@Suppress("NOTHING_TO_INLINE")
class PointArea(val size: Int) {
    @PublishedApi
    internal val points = Array(size) { Point() }
    @PublishedApi
    internal var offset = 0

    @PublishedApi
    internal fun alloc() = points[offset++]

    inline fun Point(x: Number, y: Number) = alloc().setTo(x, y)

    operator fun IPoint.plus(other: IPoint): IPoint = alloc().setToAdd(this, other)
    operator fun IPoint.minus(other: IPoint): IPoint = alloc().setToSub(this, other)

    operator fun IPoint.times(value: IPoint): IPoint = alloc().setToMul(this, value)
    inline operator fun IPoint.times(value: Number): IPoint = alloc().setToMul(this, value.toDouble())

    operator fun IPoint.div(value: IPoint): IPoint = alloc().setToDiv(this, value)
    inline operator fun IPoint.div(value: Number): IPoint = alloc().setToDiv(this, value.toDouble())

    inline operator fun invoke(callback: PointArea.() -> Unit) {
        val oldOffset = offset
        try {
            callback()
        } finally {
            offset = oldOffset
        }
    }
}
