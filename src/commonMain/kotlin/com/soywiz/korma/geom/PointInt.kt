package com.soywiz.korma.geom

import com.soywiz.korma.MVector2Int
import com.soywiz.korma.Vector2Int

@Deprecated("", replaceWith = ReplaceWith("PointInt"))
typealias IPointInt = PointInt

typealias PointInt = Vector2Int
typealias MPointInt = MVector2Int

fun IPointInt(x: Int, y: Int): Vector2Int = Vector2Int(x, y)
fun PointInt(x: Int, y: Int): Vector2Int = Vector2Int(x, y)
