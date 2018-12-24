package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.triangle.*

fun List<ITriangle>.toSpatialMesh(): SpatialMesh = SpatialMesh.fromTriangles(this)
fun List<ITriangle>.pathFind(): SpatialMeshFind = SpatialMeshFind(this.toSpatialMesh())

fun SpatialMeshFind.funnel(p0: Point2d, p1: Point2d): List<Point2d> {
    val pf = this
    val sm = pf.spatialMesh
    val pointStart = Point2d(p0.x, p0.y)
    val pointEnd = Point2d(p1.x, p1.y)
    val pathNodes = pf.find(sm.spatialNodeFromPoint(pointStart), sm.spatialNodeFromPoint(pointEnd))
    val portals = SpatialMeshFind.Channel.channelToPortals(pointStart, pointEnd, pathNodes)
    return portals.path.map { Point2d(it.x, it.y) }
}

fun List<ITriangle>.funnel(p0: Point2d, p1: Point2d): List<Point2d> = this.pathFind().funnel(p0, p1)
fun List<ITriangle>.pathFind(p0: Point2d, p1: Point2d): List<Point2d> = this.pathFind().funnel(p0, p1)

fun Shape2d.toSpatialMesh(): SpatialMesh = SpatialMesh.fromTriangles(this.triangulate())
fun Shape2d.pathFind(): SpatialMeshFind = this.triangulate().pathFind()
fun Shape2d.pathFind(p0: Point2d, p1: Point2d): List<Point2d> = this.triangulate().pathFind().funnel(p0, p1)
