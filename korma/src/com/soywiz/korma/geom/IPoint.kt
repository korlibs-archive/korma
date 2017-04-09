package com.soywiz.korma.geom

data class IPoint(var x: Int = 0, var y: Int = x) {
	fun setTo(x: Int, y: Int) = this.apply { this.x = x; this.y = y }
	fun setTo(that: IPoint) = this.setTo(that.x, that.y)

	operator fun plus(that: IPoint) = IPoint(this.x + that.x, this.y + that.y)
	operator fun minus(that: IPoint) = IPoint(this.x - that.x, this.y - that.y)
}