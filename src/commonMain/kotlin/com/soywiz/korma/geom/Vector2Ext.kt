package com.soywiz.korma.geom

import com.soywiz.korma.Vector2

fun Iterable<Vector2>.bounds(out: Rectangle = Rectangle()): Rectangle = out.setBounds(
    left = this.map { it.x }.min() ?: 0f,
    top = this.map { it.y }.min() ?: 0f,
    right = this.map { it.x }.max() ?: 0f,
    bottom = this.map { it.y }.max() ?: 0f
)
