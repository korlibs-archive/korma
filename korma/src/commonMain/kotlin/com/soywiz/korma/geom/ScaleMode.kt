package com.soywiz.korma.geom

class ScaleMode(private val function: ScaleMode.(item: Size, container: Size, target: Size) -> Unit) {
    operator fun invoke(item: Size, container: Size, target: Size = Size()): Size = target.apply {
        function(item, container, target)
    }

    operator fun invoke(item: SizeInt, container: SizeInt, target: SizeInt = SizeInt()): SizeInt = target.apply {
        function(item.asDouble(), container.asDouble(), target.asDouble())
    }

    companion object {
        val COVER = ScaleMode { item, container, target ->
            val s0 = container.width / item.width
            val s1 = container.height / item.height
            target.setTo(item).setToScaled(kotlin.math.max(s0, s1))
        }

        val SHOW_ALL = ScaleMode { item, container, target ->
            val s0 = container.width / item.width
            val s1 = container.height / item.height
            target.setTo(item).setToScaled(kotlin.math.min(s0, s1))
        }

        val EXACT = ScaleMode { item, container, target ->
            target.setTo(container)
        }

        val NO_SCALE = ScaleMode { item, container, target ->
            target.setTo(item)
        }
    }
}

fun Rectangle.applyScaleMode(container: Rectangle, mode: ScaleMode, anchor: Anchor, out: Rectangle = Rectangle()): Rectangle = this.size.applyScaleMode(container, mode, anchor, out)

fun Size.applyScaleMode(container: Rectangle, mode: ScaleMode, anchor: Anchor, out: Rectangle = Rectangle(), tempSize: Size = Size()): Rectangle {
    val outSize = this.applyScaleMode(container.size, mode, tempSize)
    out.setToAnchoredRectangle(Rectangle(0, 0, outSize.width, outSize.height), anchor, container)
    return out
}

fun SizeInt.applyScaleMode(container: RectangleInt, mode: ScaleMode, anchor: Anchor, out: RectangleInt = RectangleInt(), tempSize: SizeInt = SizeInt()): RectangleInt =
    this.asDouble().applyScaleMode(container.asDouble(), mode, anchor, out.asDouble(), tempSize.asDouble()).asInt()

fun SizeInt.applyScaleMode(container: SizeInt, mode: ScaleMode, out: SizeInt = SizeInt(0, 0)): SizeInt =
    mode(this, container, out)
fun Size.applyScaleMode(container: Size, mode: ScaleMode, out: Size = Size(0, 0)): Size =
    mode(this, container, out)

fun SizeInt.fitTo(container: SizeInt, out: SizeInt = SizeInt(0, 0)): SizeInt =
    applyScaleMode(container, ScaleMode.SHOW_ALL, out)
fun Size.fitTo(container: Size, out: Size = Size(0, 0)): Size =
    applyScaleMode(container, ScaleMode.SHOW_ALL, out)
