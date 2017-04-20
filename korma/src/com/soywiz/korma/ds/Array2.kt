package com.soywiz.korma.ds

import com.soywiz.korma.geom.IPoint
import java.util.*

@Suppress("NOTHING_TO_INLINE")
data class Array2<T>(val width: Int, val height: Int, val data: Array<T>) : Iterable<T> {
	companion object {
		//inline operator fun <reified T> invoke(width: Int, height: Int, gen: () -> T) = Array2(width, height, Array(width * height) { gen() })
		inline operator fun <reified T> invoke(width: Int, height: Int, gen: (n: Int) -> T) = Array2(width, height, Array(width * height) { gen(it) })

		//inline operator fun <reified T> invoke(width: Int, height: Int, gen: (x: Int, y: Int) -> T) = Array2(width, height, Array(width * height) { gen(it % width, it / width) })
		inline operator fun <reified T> invoke(rows: List<List<T>>): Array2<T> {
			val width = rows[0].size
			val height = rows.size
			val anyCell = rows[0][0]
			return Array2(width, height) { anyCell }.apply { set(rows) }
		}
	}

	fun set(rows: List<List<T>>) {
		var n = 0
		for (y in rows.indices) {
			val row = rows[y]
			for (x in row.indices) {
				this.data[n++] = row[x]
			}
		}
	}

	override fun equals(other: Any?): Boolean {
		return (other is Array2<*>) && this.width == other.width  && this.height == other.height && Arrays.equals(this.data, other.data)
	}

	override fun hashCode(): Int = width + height + data.hashCode()

	private inline fun index(x: Int, y: Int) = y * width + x
	private inline fun index(p: IPoint) = p.y * width + p.x

	operator fun get(x: Int, y: Int): T = data[index(x, y)]
	operator fun set(x: Int, y: Int, value: T): Unit = run { data[index(x, y)] = value }

	operator fun get(p: IPoint): T = data[index(p)]
	operator fun set(p: IPoint, value: T): Unit = run { data[index(p)] = value }

	fun inside(x: Int, y: Int): Boolean = x >= 0 && y >= 0 && x < width && y < height
	fun inside(that: IPoint): Boolean = inside(that.x, that.y)

	operator fun contains(v: T): Boolean = this.data.contains(v)

	inline fun each(callback: (x: Int, y: Int, v: T) -> Unit) {
		var n = 0
		for (y in 0 until height) {
			for (x in 0 until width) {
				callback(x, y, data[n++])
			}
		}
	}

	inline fun fill(gen: (old: T) -> T) {
		var n = 0
		for (y in 0 until height) {
			for (x in 0 until width) {
				data[n] = gen(data[n])
				n++
			}
		}
	}

	fun getPositionsWithValue(value: T) = data.indices.filter { data[it] == value }.map { IPoint(it % width, it / width) }

	fun clone() = Array2(width, height, data.clone())

	fun dump() {
		for (y in 0 until height) {
			for (x in 0 until width) {
				print(this[x, y])
			}
			println()
		}
	}

	override fun iterator(): Iterator<T> = data.iterator()
}