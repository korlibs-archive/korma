package de.lighti.clipper

import org.junit.Assert
import org.junit.Test

class ClipperTest {
	@Test
	fun name() {
		val clipper = DefaultClipper()
		val path1 = Path(4)
		path1.add(Point.LongPoint(0, 0))
		path1.add(Point.LongPoint(10, 0))
		path1.add(Point.LongPoint(10, 10))
		path1.add(Point.LongPoint(0, 10))

		val path2 = Path(4)
		path2.add(Point.LongPoint(5 + 0, 0))
		path2.add(Point.LongPoint(5 + 10, 0))
		path2.add(Point.LongPoint(5 + 10, 10))
		path2.add(Point.LongPoint(5 + 0, 10))

		val paths = Paths()

		clipper.addPath(path1, Clipper.PolyType.CLIP, true)
		clipper.addPath(path2, Clipper.PolyType.SUBJECT, true)
		clipper.execute(Clipper.ClipType.INTERSECTION, paths)

		Assert.assertEquals("[[Point [x=10, y=10, z=0], Point [x=5, y=10, z=0], Point [x=5, y=0, z=0], Point [x=10, y=0, z=0]]]", paths.toString())
	}
}