package com.soywiz.korma.geom.clipper

import com.soywiz.korma.*
import com.soywiz.korma.geom.shape.internal.*
import kotlin.test.*

class ClipperTest {
    @Test
    fun name() {
        val clipper = DefaultClipper()
        val path1 = Path(Vector2(0, 0), Vector2(10, 0), Vector2(10, 10), Vector2(0, 10))
        val path2 = Path(Vector2(5 + 0, 0), Vector2(5 + 10, 0), Vector2(5 + 10, 10), Vector2(5 + 0, 10))
        val paths = Paths()

        clipper.addPath(path1, Clipper.PolyType.CLIP, true)
        clipper.addPath(path2, Clipper.PolyType.SUBJECT, true)
        clipper.execute(Clipper.ClipType.INTERSECTION, paths)

        assertEquals("[[(10, 10), (5, 10), (5, 0), (10, 0)]]", paths.toString())
        assertEquals("Rectangle(x=5, y=0, width=5, height=10)", paths.bounds.toString())
    }
}
