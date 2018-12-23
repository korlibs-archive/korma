package com.soywiz.korma.geom

import com.soywiz.korma.*
import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*
import com.soywiz.korma.math.*

interface IRectangle {
    val x: Double
    val y: Double
    val width: Double
    val height: Double
}

data class Rectangle(
    override var x: Double = 0.0, override var y: Double = 0.0,
    override var width: Double = 0.0, override var height: Double = 0.0
) : MutableInterpolable<Rectangle>, Interpolable<Rectangle>, IRectangle, Sizeable {
    data class Immutable(
        override val x: Double,
        override val y: Double,
        override val width: Double,
        override val height: Double
    ) : IRectangle {
        fun toMutable() = Rectangle(x, y, width, height)
    }

    fun toImmutable() = Immutable(x, y, width, height)

    constructor(x: Int, y: Int, width: Int, height: Int) : this(
        x.toDouble(),
        y.toDouble(),
        width.toDouble(),
        height.toDouble()
    )

    val isEmpty: Boolean get() = area == 0.0
    val isNotEmpty: Boolean get() = area != 0.0
    val area: Double get() = width * height
    var left: Double; get() = x; set(value) = run { x = value }
    var top: Double; get() = y; set(value) = run { y = value }
    var right: Double; get() = x + width; set(value) = run { width = value - x }
    var bottom: Double; get() = y + height; set(value) = run { height = value - y }

    override val size: Size get() = Size(width, height)

    inline fun setTo(x: Number, y: Number, width: Number, height: Number) =
        this.setTo(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

    fun setTo(x: Double, y: Double, width: Double, height: Double) = this.apply {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun copyFrom(that: Rectangle) = setTo(that.x, that.y, that.width, that.height)

    fun setBounds(left: Double, top: Double, right: Double, bottom: Double) =
        setTo(left, top, right - left, bottom - top)

    fun setBounds(left: Int, top: Int, right: Int, bottom: Int) = setTo(left, top, right - left, bottom - top)

    operator fun times(scale: Double) = Rectangle(x * scale, y * scale, width * scale, height * scale)
    operator fun div(scale: Double) = Rectangle(x / scale, y / scale, width / scale, height / scale)

    operator fun contains(that: Rectangle) = isContainedIn(that, this)
    operator fun contains(that: Point2d) = contains(x, y)
    fun contains(x: Double, y: Double) = (x >= left && x < right) && (y >= top && y < bottom)

    infix fun intersects(that: Rectangle): Boolean = intersectsX(that) && intersectsY(that)

    infix fun intersectsX(that: Rectangle): Boolean = that.left <= this.right && that.right >= this.left
    infix fun intersectsY(that: Rectangle): Boolean = that.top <= this.bottom && that.bottom >= this.top

    fun setToIntersection(a: Rectangle, b: Rectangle) = this.apply { a.intersection(b, this) }

    infix fun intersection(that: Rectangle) = intersection(that, Rectangle())

    fun intersection(that: Rectangle, target: Rectangle = Rectangle()) = if (this intersects that) target.setBounds(
        kotlin.math.max(this.left, that.left), kotlin.math.max(this.top, that.top),
        kotlin.math.min(this.right, that.right), kotlin.math.min(this.bottom, that.bottom)
    ) else null

    fun displaced(dx: Double, dy: Double) = Rectangle(this.x + dx, this.y + dy, width, height)
    fun displace(dx: Double, dy: Double) = setTo(this.x + dx, this.y + dy, this.width, this.height)

    fun inflate(dx: Double, dy: Double) {
        x -= dx; width += 2 * dx
        y -= dy; height += 2 * dy
    }

    fun clone() = Rectangle(x, y, width, height)

    fun setToAnchoredRectangle(small: Rectangle, anchor: Anchor, big: Rectangle) = setTo(
        anchor.sx * (big.width - small.width),
        anchor.sy * (big.height - small.height),
        small.width,
        small.height
    )

    //override fun toString(): String = "Rectangle([${left.niceStr}, ${top.niceStr}]-[${right.niceStr}, ${bottom.niceStr}])"
    override fun toString(): String =
        "Rectangle(x=${x.niceStr}, y=${y.niceStr}, width=${width.niceStr}, height=${height.niceStr})"

    fun toStringBounds(): String =
        "Rectangle([${left.niceStr},${top.niceStr}]-[${right.niceStr},${bottom.niceStr}])"

    companion object {
        fun fromBounds(left: Double, top: Double, right: Double, bottom: Double): Rectangle =
            Rectangle().setBounds(left, top, right, bottom)

        fun fromBounds(left: Int, top: Int, right: Int, bottom: Int): Rectangle =
            Rectangle().setBounds(left, top, right, bottom)

        fun isContainedIn(a: Rectangle, b: Rectangle): Boolean =
            a.x >= b.x && a.y >= b.y && a.x + a.width <= b.x + b.width && a.y + a.height <= b.y + b.height
    }

    override fun interpolateWith(other: Rectangle, ratio: Double): Rectangle =
        Rectangle().setToInterpolated(this, other, ratio)

    override fun setToInterpolated(l: Rectangle, r: Rectangle, ratio: Double): Rectangle = this.setTo(
        ratio.interpolate(l.x, r.x),
        ratio.interpolate(l.y, r.y),
        ratio.interpolate(l.width, r.width),
        ratio.interpolate(l.height, r.height)
    )

    fun getAnchoredPosition(anchor: Anchor, out: MVector2 = MVector2()): MVector2 =
        out.setTo(left + width * anchor.sx, top + height * anchor.sy)

    fun toInt() = RectangleInt(x, y, width, height)
}

// @TODO: Check if this avoid boxing!
inline fun Rectangle(x: Number, y: Number, width: Number, height: Number) =
    Rectangle(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())

inline fun IRectangle(x: Number, y: Number, width: Number, height: Number) =
    Rectangle.Immutable(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())


//////////// INT


interface IRectangleInt {
    val x: Int
    val y: Int
    val width: Int
    val height: Int
}

data class RectangleInt(val position: MPositionInt, val size: SizeInt) : IRectangleInt {
    constructor(x: Int = 0, y: Int = 0, width: Int = 0, height: Int = 0) : this(
        MPositionInt(x, y),
        SizeInt(width, height)
    )

    companion object {
        fun fromBounds(left: Int, top: Int, right: Int, bottom: Int): RectangleInt =
            RectangleInt(left, top, right - left, bottom - top)
    }

    data class Immutable(override val x: Int, override val y: Int, override val width: Int, override val height: Int) :
        IRectangleInt {
        fun toMutable() = RectangleInt(x, y, width, height)
    }

    fun toImmutable() = Immutable(x, y, width, height)

    override var x: Int set(value) = run { position.x = value }; get() = position.x
    override var y: Int set(value) = run { position.y = value }; get() = position.y

    override var width: Int set(value) = run { size.width = value }; get() = size.width
    override var height: Int set(value) = run { size.height = value }; get() = size.height

    var left: Int set(value) = run { x = value }; get() = x
    var top: Int set(value) = run { y = value }; get() = y

    var right: Int set(value) = run { width = value - x }; get() = x + width
    var bottom: Int set(value) = run { height = value - y }; get() = y + height

    fun setTo(that: RectangleInt) = setTo(that.x, that.y, that.width, that.height)

    fun setTo(x: Int, y: Int, width: Int, height: Int) = this.apply {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun setPosition(x: Int, y: Int) = this.apply { this.position.setTo(x, y) }

    fun setSize(width: Int, height: Int) = this.apply {
        this.size.setTo(width, height)
        this.width = width
        this.height = height
    }

    fun setBoundsTo(left: Int, top: Int, right: Int, bottom: Int) = setTo(left, top, right - left, bottom - top)

    fun anchoredIn(container: RectangleInt, anchor: Anchor, out: RectangleInt = RectangleInt()): RectangleInt =
        out.setTo(
            ((container.width - this.width) * anchor.sx).toInt(),
            ((container.height - this.height) * anchor.sy).toInt(),
            width,
            height
        )

    fun getAnchorPosition(anchor: Anchor, out: MPositionInt = MPositionInt()): MPositionInt =
        out.setTo((x + width * anchor.sx).toInt(), (y + height * anchor.sy).toInt())

    operator fun contains(v: SizeInt): Boolean = (v.width <= width) && (v.height <= height)

    fun toDouble() = Rectangle(x, y, width, height)

    override fun toString(): String = "IRectangle(x=$x, y=$y, width=$width, height=$height)"
}

val IRectangle.int get() = RectangleInt(x, y, width, height)
val IRectangleInt.double get() = Rectangle(x, y, width, height)

fun IRectangleInt.anchor(ax: Double, ay: Double): Vector2Int =
    PointInt((x + width * ax).toInt(), (y + height * ay).toInt())

val IRectangleInt.center get() = anchor(0.5, 0.5)

inline fun RectangleInt(x: Number, y: Number, width: Number, height: Number) =
    RectangleInt(x.toInt(), y.toInt(), width.toInt(), height.toInt())

inline fun IRectangleInt(x: Number, y: Number, width: Number, height: Number) =
    RectangleInt.Immutable(x.toInt(), y.toInt(), width.toInt(), height.toInt())

///////////////////////////

fun Iterable<Rectangle>.bounds(target: Rectangle = Rectangle()): Rectangle {
    var first = true
    var left = 0.0
    var right = 0.0
    var top = 0.0
    var bottom = 0.0
    for (r in this) {
        if (first) {
            left = r.left
            right = r.right
            top = r.top
            bottom = r.bottom
            first = false
        } else {
            left = kotlin.math.min(left, r.left)
            right = kotlin.math.max(right, r.right)
            top = kotlin.math.min(top, r.top)
            bottom = kotlin.math.max(bottom, r.bottom)
        }
    }
    return target.setBounds(left, top, right, bottom)
}
