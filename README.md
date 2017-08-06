# Kotlin cORoutines MAthematics library

[![Build Status](https://travis-ci.org/soywiz/korma.svg?branch=master)](https://travis-ci.org/soywiz/korma)
[![Maven Version](https://img.shields.io/github/tag/soywiz/korma.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22korma%22)

![](https://raw.githubusercontent.com/soywiz/kor/master/logos/128/korma.png)

This is a mathematical library. Mostly focused on geometry. It is not related to kotlin coroutines at all.
But it is part of the [soywiz's korlibs](https://github.com/soywiz/korlibs) libraries and thus the name.

This library includes:

* Geometry 2d primitives (Matrix2d/AffineTransform, Point2d, Rectangle, Size, Anchor, ScaleMode)
* Mutable and Immutable interpolation facilities and integration with primitives
* Bezier curves quad and cubic calculations/bounds/length
* VectorPath to define vector shapes with lines and bezier quad and cubic curves with fully-featured canvas-like API to creating complex shapes
* BinPacker using max rects to allocate rectangles in bidimensional spaces (eg. Atlas Generation/Allocation)
* [Clipper](https://sourceforge.net/projects/polyclipping/) (includes an embedded version of the awesome clipper library ported to Kotlin) to do boolean operation on polygons union, intersection, subtraction, xor, collision between two shapes) + growing/shrinking
* poly2tri library to convert polygons to simple triangles
* TA* + Funnel algorithms integrated with poly2tri to do pathfinding in a SpatialMesh that can be obtained from a Shape2d or VectorShape
* Shape2d classes with a simplified interface completely integrated with clipper and poly2tri to perform boolean operations (union, intersection, subtraction, xor, collision between two shapes) + growing/shrinking + triangulation + pathfinding in arbitrary shapes + approximating VectorPath to Polygons/Shape2d
* Simple A* implementation