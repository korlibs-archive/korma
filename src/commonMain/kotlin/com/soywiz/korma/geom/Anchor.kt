package com.soywiz.korma.geom

import com.soywiz.korma.interpolation.Interpolable
import com.soywiz.korma.interpolation.interpolate

data class Anchor(val sx: Float, val sy: Float) : Interpolable<Anchor> {
    companion object {
        inline operator fun invoke(sx: Number, sy: Number) = Anchor(sx.toFloat(), sy.toFloat())

        val TOP_LEFT = Anchor(0f, 0f)
        val TOP_CENTER = Anchor(.5f, 0f)
        val TOP_RIGHT = Anchor(1f, 0f)

        val MIDDLE_LEFT = Anchor(0f, .5f)
        val MIDDLE_CENTER = Anchor(.5f, .5f)
        val MIDDLE_RIGHT = Anchor(1f, .5f)

        val BOTTOM_LEFT = Anchor(0, 1)
        val BOTTOM_CENTER = Anchor(.5f, 1f)
        val BOTTOM_RIGHT = Anchor(1f, 1f)
    }

    override fun interpolateWith(other: Anchor, ratio: Float): Anchor = Anchor(
        ratio.interpolate(this.sx, other.sx),
        ratio.interpolate(this.sy, other.sy)
    )
}
