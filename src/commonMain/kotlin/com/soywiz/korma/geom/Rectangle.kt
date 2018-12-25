package com.soywiz.korma.geom

import com.soywiz.korma.internal.*
import com.soywiz.korma.interpolation.*

interface IRectangle {
    val x: Float
    val y: Float
    val width: Float
    val height: Float

    companion object {
        inline operator fun invoke(x: Number, y: Number, width: Number, height: Number): IRectangle = Rectangle(x, y, width, height)
    }
}

val IRectangle.left get() = x
val IRectangle.top get() = y
val IRectangle.right get() = x + width
val IRectangle.bottom get() = y + height

data class Rectangle(
    override var x: Float, override var y: Float,
    override var width: Float, override var height: Float
) : MutableInterpolable<Rectangle>, Interpolable<Rectangle>, IRectangle, Sizeable {
    companion object {
        inline operator fun invoke(): Rectangle = Rectangle(0f, 0f, 0f, 0f)
        inline operator fun invoke(x: Number, y: Number, width: Number, height: Number): Rectangle = Rectangle(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())
        inline fun fromBounds(left: Number, top: Number, right: Number, bottom: Number): Rectangle = Rectangle().setBounds(left, top, right, bottom)
        fun isContainedIn(a: Rectangle, b: Rectangle): Boolean = a.x >= b.x && a.y >= b.y && a.x + a.width <= b.x + b.width && a.y + a.height <= b.y + b.height
    }

    val isEmpty: Boolean get() = area == 0f
    val isNotEmpty: Boolean get() = area != 0f
    val area: Float get() = width * height
    var left: Float; get() = x; set(value) = run { x = value }
    var top: Float; get() = y; set(value) = run { y = value }
    var right: Float; get() = x + width; set(value) = run { width = value - x }
    var bottom: Float; get() = y + height; set(value) = run { height = value - y }

    override val size: Size get() = Size(width, height)

    fun setTo(x: Float, y: Float, width: Float, height: Float) = this.apply {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    fun copyFrom(that: Rectangle) = setTo(that.x, that.y, that.width, that.height)

    fun setBounds(left: Float, top: Float, right: Float, bottom: Float) = setTo(left, top, right - left, bottom - top)

    operator fun times(scale: Float) = Rectangle(x * scale, y * scale, width * scale, height * scale)
    operator fun div(scale: Float) = Rectangle(x / scale, y / scale, width / scale, height / scale)

    operator fun contains(that: Rectangle) = isContainedIn(that, this)
    operator fun contains(that: IPoint) = contains(that.x, that.y)
    fun contains(x: Float, y: Float) = (x >= left && x < right) && (y >= top && y < bottom)

    infix fun intersects(that: Rectangle): Boolean = intersectsX(that) && intersectsY(that)

    infix fun intersectsX(that: Rectangle): Boolean = that.left <= this.right && that.right >= this.left
    infix fun intersectsY(that: Rectangle): Boolean = that.top <= this.bottom && that.bottom >= this.top

    fun setToIntersection(a: Rectangle, b: Rectangle) = this.apply { a.intersection(b, this) }

    infix fun intersection(that: Rectangle) = intersection(that, Rectangle())

    fun intersection(that: Rectangle, target: Rectangle = Rectangle()) = if (this intersects that) target.setBounds(
        kotlin.math.max(this.left, that.left), kotlin.math.max(this.top, that.top),
        kotlin.math.min(this.right, that.right), kotlin.math.min(this.bottom, that.bottom)
    ) else null

    fun displaced(dx: Float, dy: Float) = Rectangle(this.x + dx, this.y + dy, width, height)
    fun displace(dx: Float, dy: Float) = setTo(this.x + dx, this.y + dy, this.width, this.height)

    fun inflate(dx: Float, dy: Float) {
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

    override fun interpolateWith(other: Rectangle, ratio: Float): Rectangle =
        Rectangle().setToInterpolated(this, other, ratio)

    override fun setToInterpolated(l: Rectangle, r: Rectangle, ratio: Float): Rectangle = this.setTo(
        ratio.interpolate(l.x, r.x),
        ratio.interpolate(l.y, r.y),
        ratio.interpolate(l.width, r.width),
        ratio.interpolate(l.height, r.height)
    )

    fun getAnchoredPosition(anchor: Anchor, out: Point = Point()): Point =
        out.setTo(left + width * anchor.sx, top + height * anchor.sy)

    fun toInt() = RectangleInt(x, y, width, height)
}

inline fun Rectangle.setTo(x: Number, y: Number, width: Number, height: Number) =
    this.setTo(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat())

inline fun Rectangle.setBounds(left: Number, top: Number, right: Number, bottom: Number) = setBounds(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
inline operator fun Rectangle.times(scale: Number) = times(scale.toFloat())
inline operator fun Rectangle.div(scale: Number) = div(scale.toFloat())
inline fun Rectangle.contains(x: Number, y: Number) = contains(x.toFloat(), y.toFloat())

inline fun Rectangle.displaced(dx: Number, dy: Number) = displaced(dx.toFloat(), dy.toFloat())
inline fun Rectangle.displace(dx: Number, dy: Number) = displace(dx.toFloat(), dy.toFloat())
inline fun Rectangle.inflate(dx: Number, dy: Number) = inflate(dx.toFloat(), dy.toFloat())

//////////// INT

interface IRectangleInt {
    val x: Int
    val y: Int
    val width: Int
    val height: Int

    companion object {
        inline operator fun invoke(x: Number, y: Number, width: Number, height: Number): IRectangleInt = RectangleInt(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }
}

val IRectangleInt.left get() = x
val IRectangleInt.top get() = y
val IRectangleInt.right get() = x + width
val IRectangleInt.bottom get() = y + height

inline class RectangleInt(val rect: Rectangle) : IRectangleInt {
    override var x: Int
        set(value) = run { rect.x = value.toFloat() }
        get() = rect.x.toInt()

    override var y: Int
        set(value) = run { rect.y = value.toFloat() }
        get() = rect.y.toInt()

    override var width: Int
        set(value) = run { rect.width = value.toFloat() }
        get() = rect.width.toInt()

    override var height: Int
        set(value) = run { rect.height = value.toFloat() }
        get() = rect.height.toInt()

    var left: Int
        set(value) = run { rect.left = value.toFloat() }
        get() = rect.left.toInt()

    var top: Int
        set(value) = run { rect.top = value.toFloat() }
        get() = rect.top.toInt()

    var right: Int
        set(value) = run { rect.right = value.toFloat() }
        get() = rect.right.toInt()

    var bottom: Int
        set(value) = run { rect.bottom = value.toFloat() }
        get() = rect.bottom.toInt()

    companion object {
        operator fun invoke() = RectangleInt(Rectangle())
        inline operator fun invoke(x: Number, y: Number, width: Number, height: Number) = RectangleInt(Rectangle(x, y, width, height))

        fun fromBounds(left: Int, top: Int, right: Int, bottom: Int): RectangleInt =
            RectangleInt(left, top, right - left, bottom - top)
    }

    override fun toString(): String = "Rectangle(x=$x, y=$y, width=$width, height=$height)"
}

fun RectangleInt.setTo(that: RectangleInt) = setTo(that.x, that.y, that.width, that.height)

fun RectangleInt.setTo(x: Int, y: Int, width: Int, height: Int) = this.apply {
    this.x = x
    this.y = y
    this.width = width
    this.height = height
}

fun RectangleInt.setPosition(x: Int, y: Int) = this.apply { this.x = x; this.y = y }

fun RectangleInt.setSize(width: Int, height: Int) = this.apply {
    this.width = width
    this.height = height
}

fun RectangleInt.setBoundsTo(left: Int, top: Int, right: Int, bottom: Int) = setTo(left, top, right - left, bottom - top)

////////////////////

operator fun IRectangleInt.contains(v: SizeInt): Boolean = (v.width <= width) && (v.height <= height)

fun IRectangleInt.anchoredIn(container: RectangleInt, anchor: Anchor, out: RectangleInt = RectangleInt()): RectangleInt =
    out.setTo(
        ((container.width - this.width) * anchor.sx).toInt(),
        ((container.height - this.height) * anchor.sy).toInt(),
        width,
        height
    )

fun IRectangleInt.getAnchorPosition(anchor: Anchor, out: PointInt = PointInt()): PointInt =
    out.setTo((x + width * anchor.sx).toInt(), (y + height * anchor.sy).toInt())

fun Rectangle.asInt() = RectangleInt(this)
fun RectangleInt.asFloat() = this.rect

val IRectangle.int get() = RectangleInt(x, y, width, height)
val IRectangleInt.float get() = Rectangle(x, y, width, height)

fun IRectangleInt.anchor(ax: Float, ay: Float): IPointInt =
    PointInt((x + width * ax).toInt(), (y + height * ay).toInt())

inline fun IRectangleInt.anchor(ax: Number, ay: Number): IPointInt = anchor(ax.toFloat(), ay.toFloat())

val IRectangleInt.center get() = anchor(0.5, 0.5)

///////////////////////////

fun Iterable<Rectangle>.bounds(target: Rectangle = Rectangle()): Rectangle {
    var first = true
    var left = 0f
    var right = 0f
    var top = 0f
    var bottom = 0f
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
