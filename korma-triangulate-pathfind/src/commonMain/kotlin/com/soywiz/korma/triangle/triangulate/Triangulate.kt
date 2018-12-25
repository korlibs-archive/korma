package com.soywiz.korma.triangle.triangulate

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.geom.triangle.*
import com.soywiz.korma.triangle.internal.*

fun List<Point2d>.triangulate(): List<Triangle> {
    val sc = SweepContext(this)
    val s = Sweep(sc)
    s.triangulate()
    return sc.triangles.toList()
}

fun Shape2d.triangulate(): List<Triangle> = this.getAllPoints().toPoints().triangulate()
