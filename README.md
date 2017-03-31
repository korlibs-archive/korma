# Kotlin cORoutines MAthematics library

This is a mathematical library. Mostly focused on geometry. It is not related to kotlin coroutines at all.
But it is part of the [soywiz's korlibs](https://github.com/soywiz/korlibs) libraries and thus the name.

This library includes:

* Geometry 2d primitives (Matrix2d/AffineTransform, Point2d, Rectangle, Size, Anchor, ScaleMode)
* Interpolation facilities
* Bezier curves calculations
* VectorPath to define vector shapes with lines and bezier curves
* BinPacker using max rects to allocate rectangles in a bidimensional space
* [Clipper](https://sourceforge.net/projects/polyclipping/) (includes an embedded version of the awesome clipper library ported to Kotlin) to do boolean operation on polygons + growing
* poly2tri library to convert polygons to simple triangles
* funnel algorithm integrated with poly2tri to do pathfinding in a SpatialMesh
* Shape2d classes integrated with clipper and poly2tri to perform boolean operations + growing + triangulation + pathfinding of arbitrary shapes + rasterizing VectorPath
