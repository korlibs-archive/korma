package com.soywiz.korma.geom.ds

import com.soywiz.kds.*
import com.soywiz.korma.geom.*
import kotlin.math.*

interface IPointArrayList {
    val size: Int
    fun getX(index: Int): Double
    fun getY(index: Int): Double
}

class PointArrayList(capacity: Int = 7) : IPointArrayList {
    private val xList = DoubleArrayList(capacity)
    private val yList = DoubleArrayList(capacity)
    override val size get() = xList.size

    fun isEmpty() = size == 0
    fun isNotEmpty() = size != 0

    companion object {
        operator fun invoke(capacity: Int = 7, callback: PointArrayList.() -> Unit): PointArrayList = PointArrayList(capacity).apply(callback)
        operator fun invoke(points: List<Point2d>): PointArrayList = PointArrayList(points.size) {
            for (n in points.indices) add(points[n].x, points[n].y)
        }
    }

    fun add(x: Double, y: Double) = this.apply {
        xList += x
        yList += y
    }

    inline fun add(x: Number, y: Number) = add(x.toDouble(), y.toDouble())

    fun add(p: Point2d) = add(p.x, p.y)

    fun add(other: IPointArrayList) = this.apply {
        for (n in 0 until other.size) add(other.getX(n), other.getY(n))
    }

    override fun getX(index: Int) = xList[index]
    override fun getY(index: Int) = yList[index]

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('[')
        for (n in 0 until size) {
            val x = getX(n)
            val y = getY(n)
            if (n != 0) {
                sb.append(", ")
            }
            sb.append('(')
            if (x == round(x)) sb.append(x.toInt()) else sb.append(x)
            sb.append(", ")
            if (y == round(y)) sb.append(y.toInt()) else sb.append(y)
            sb.append(')')
        }
        sb.append(']')
        return sb.toString()
    }

    fun swap(indexA: Int, indexB: Int) {
        xList.swap(indexA, indexB)
        yList.swap(indexA, indexB)
    }
}

fun DoubleArrayList.swap(indexA: Int, indexB: Int) {
    val tmp = this[indexA]
    this[indexA] = this[indexB]
    this[indexB] = tmp
}

fun IPointArrayList.getPoint(index: Int) = Point2d(getX(index), getY(index))
fun IPointArrayList.toPoints() = (0 until size).map { getPoint(it) }
fun IPointArrayList.contains(x: Double, y: Double): Boolean {
    for (n in 0 until size) if (getX(n) == x && getY(n) == y) return true
    return false
}

inline fun IPointArrayList.contains(x: Number, y: Number): Boolean = contains(x.toDouble(), y.toDouble())
