package com.soywiz.korma.geom.shape

import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.ds.*
import com.soywiz.korma.geom.vector.*
import kotlin.math.*
import kotlin.test.*

class Shape2dTest {
    @Test
    fun name() {
        assertEquals(
            "Rectangle(x=5, y=0, width=5, height=10)",
            (Shape2d.Rectangle(0, 0, 10, 10) intersection Shape2d.Rectangle(5, 0, 10, 10)).toString()
        )

        assertEquals(
            "Polygon(points=[(10, 5), (15, 5), (15, 15), (5, 15), (5, 10), (0, 10), (0, 0), (10, 0)])",
            (Shape2d.Rectangle(0, 0, 10, 10) union Shape2d.Rectangle(5, 5, 10, 10)).toString()
        )

        assertEquals(
            "Complex(items=[Rectangle(x=10, y=0, width=5, height=10), Rectangle(x=0, y=0, width=5, height=10)])",
            (Shape2d.Rectangle(0, 0, 10, 10) xor Shape2d.Rectangle(5, 0, 10, 10)).toString()
        )
    }

    @Test
    fun extend() {
        assertEquals(
            "Rectangle(x=-10, y=-10, width=30, height=30)",
            (Shape2d.Rectangle(0, 0, 10, 10).extend(10.0)).toString()
        )
    }

    @Test
    fun vectorPathToShape2d() {
        val exactArea = Shape2d.Circle(0, 0, 100).area
        val vp = VectorPath().apply { circle(0, 0, 100) }
        val shape = vp.toShape2d()
        assertEquals(true, shape.closed)
        assertTrue(abs(exactArea - shape.area) / exactArea < 0.01)
        assertEquals(77, shape.paths.totalVertices)
    }

    @Test
    fun triangulate() {
        val shape = Rectangle(0, 0, 100, 100).toShape()
        //println(shape)
        //println(shape.getAllPoints())
        //println(shape.getAllPoints().toPoints())
        assertEquals(
            "[Triangle((0, 100), (100, 0), (100, 100)), Triangle((0, 100), (0, 0), (100, 0))]",
            shape.triangulate().toString()
        )
    }

    @Test
    fun pathFind() {
        assertEquals(
            "[(10, 10), (90, 90)]",
            Rectangle(0, 0, 100, 100).toShape().pathFind(Point2d(10, 10), Point2d(90, 90)).toString()
        )
        assertEquals(
            "[(10, 10), (100, 50), (120, 52)]",
            (Rectangle(0, 0, 100, 100).toShape() + Rectangle(100, 50, 50, 50).toShape()).pathFind(
                Point2d(10, 10),
                Point2d(120, 52)
            ).toString()
        )
    }
}
