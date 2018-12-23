package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*
import com.soywiz.korma.math.*

interface ISize {
    val width: Double
    val height: Double
}

data class Size(override var width: Double, override var height: Double) : MutableInterpolable<Size>,
    Interpolable<Size>, Sizeable, ISize {
    data class Immutable(override val width: Double, override val height: Double) : ISize

    override val size: Size = this

    fun setTo(width: Double, height: Double) = this.apply {
        this.width = width
        this.height = height
    }

    val area: Double get() = width * height
    val perimeter: Double get() = width * 2 + height * 2
    val min: Double get() = kotlin.math.min(width, height)
    val max: Double get() = kotlin.math.max(width, height)

    fun clone() = Size(width, height)

    override fun interpolateWith(other: Size, ratio: Double): Size = Size(0, 0).setToInterpolated(this, other, ratio)

    override fun setToInterpolated(l: Size, r: Size, ratio: Double): Size = this.setTo(
        ratio.interpolate(l.width, r.width),
        ratio.interpolate(l.height, r.height)
    )

    override fun toString(): String = "Size(width=${width.niceStr}, height=${height.niceStr})"
}

inline fun Size(width: Number, height: Number) = Size(width.toDouble(), height.toDouble())
inline fun ISize(width: Number, height: Number) = Size.Immutable(width.toDouble(), height.toDouble())

data class SizeInt(var width: Int = 0, var height: Int = width) {
    operator fun contains(v: SizeInt): Boolean = (v.width <= width) && (v.height <= height)

    operator fun times(v: Double) = SizeInt((width * v).toInt(), (height * v).toInt())

    fun setTo(width: Int, height: Int) = this.apply {
        this.width = width
        this.height = height
    }

    fun setTo(that: SizeInt) = setTo(that.width, that.height)

    fun applyScaleMode(container: SizeInt, mode: ScaleMode, out: SizeInt = SizeInt()): SizeInt =
        mode(this, container, out)

    fun fitTo(container: SizeInt, out: SizeInt = SizeInt()): SizeInt =
        applyScaleMode(container, ScaleMode.SHOW_ALL, out)

    fun setToScaled(sx: Double, sy: Double = sx) = setTo((this.width * sx).toInt(), (this.height * sy).toInt())

    fun anchoredIn(container: RectangleInt, anchor: Anchor, out: RectangleInt = RectangleInt()): RectangleInt {
        return out.setTo(
            ((container.width - this.width) * anchor.sx).toInt(),
            ((container.height - this.height) * anchor.sy).toInt(),
            width,
            height
        )
    }

    fun getAnchorPosition(anchor: Anchor, out: MPositionInt = MPositionInt()): MPositionInt =
        out.setTo((width * anchor.sx).toInt(), (height * anchor.sy).toInt())
}

interface Sizeable {
    val size: Size
}
