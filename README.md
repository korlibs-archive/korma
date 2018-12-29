# ![KorMA](https://raw.githubusercontent.com/korlibs/korlibs-logos/master/256/korma.png)

Korma is a Mathematical Library mostly focused on geometry for Multiplatform Kotlin 1.3.

It includes structures for Points and Matrices (2D and 3D), Typed Angles, Rectangles, BoundsBuilder, Anchors, Vector graphics with Bezier curves support and Context2D-like API for building vectors, Interpolation facilities, Easing, Triangulation, BinPacking and Path Finding in Bidimensional arrays and Triangulated Spatial Meshes.

[![Build Status](https://travis-ci.org/korlibs/korma.svg?branch=master)](https://travis-ci.org/korlibs/korma)
[![Maven Version](https://img.shields.io/github/tag/korlibs/korma.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22korma%22)

### Full Documentation: https://korlibs.soywiz.com/korma/

### Some samples:

```kotlin
val vector = VectorPath {
    // Here we can use moveTo, lineTo, quadTo, cubicTo, circle, ellipse, arc...
    rect(0, 0, 100, 100)
    rect(300, 0, 100, 100)
}.triangulate().toString()
// "[[Triangle((0, 100), (100, 0), (100, 100)), Triangle((0, 100), (0, 0), (100, 0))], [Triangle((300, 100), (400, 0), (400, 100)), Triangle((300, 100), (300, 0), (400, 0))]]"

// Angles
val angle = 90.degrees
val angleInRadians = angle.radians

// Matrices
val a = Matrix(2, 1, 1, 2, 10, 10)
val b = a.inverted()
assertEquals(identity, a * b)

// Rectangle + ScaleMode + Anchor
assertEquals(Rectangle(0, -150, 600, 600), Size(100, 100).applyScaleMode(Rectangle(0, 0, 600, 300), ScaleMode.COVER, Anchor.MIDDLE_CENTER))

// PathFinding (Matrix)
val points = AStar.find(
    board = Array2("""
        .#....
        .#.##.
        .#.#..
        ...#..
    """) { c, x, y -> c == '#' },
    x0 = 0,
    y0 = 0,
    x1 = 4,
    y1 = 2,
    findClosest = false
)
println(points)
// [(0, 0), (0, 1), (0, 2), (0, 3), (1, 3), (2, 3), (2, 2), (2, 1), (2, 0), (3, 0), (4, 0), (5, 0), (5, 1), (5, 2), (4, 2)]

// PathFinding (Shape)
assertEquals(
    "[(10, 10), (100, 50), (120, 52)]",
    (Rectangle(0, 0, 100, 100).toShape() + Rectangle(100, 50, 50, 50).toShape()).pathFind(
        IPoint(10, 10),
        IPoint(120, 52)
    ).toString()
)
```

### Usage with gradle:

```kotlin
def kormaVersion = "1.0.0"

repositories {
    maven { url "https://dl.bintray.com/soywiz/soywiz" }
}

dependencies {
    // For multiplatform projects
    implementation "com.soywiz:korma:$kormaVersion"
    
    // For JVM/Android only
    implementation "com.soywiz:korma-jvm:$kormaVersion"
    // For JS only
    implementation "com.soywiz:korma-js:$kormaVersion"
}

// Additional funcionality using Clipper and poly2try code (with separate licenses):
// - https://github.com/korlibs/korma/blob/master/korma-shape-ops/LICENSE
// - https://github.com/korlibs/korma/blob/master/korma-triangulate-pathfind/LICENSE
dependencies {
    implementation "com.soywiz:korma-shape-ops:$kormaVersion"
    implementation "com.soywiz:korma-triangulate-pathfind:$kormaVersion"
}

// settigs.gradle
enableFeaturePreview('GRADLE_METADATA')
```
