@file:Suppress("NOTHING_TO_INLINE")

package com.soywiz.korma.geom

import com.soywiz.korma.interpolation.Interpolable
import com.soywiz.korma.interpolation.MutableInterpolable
import com.soywiz.korma.interpolation.interpolate
import kotlin.math.*

interface IMatrix {
    val a: Float
    val b: Float
    val c: Float
    val d: Float
    val tx: Float
    val ty: Float
}

inline fun IMatrix(a: Number, b: Number, c: Number, d: Number, tx: Number, ty: Number) = Matrix.Immutable(
    a.toFloat(),
    b.toFloat(),
    c.toFloat(),
    d.toFloat(),
    tx.toFloat(),
    ty.toFloat()
)

data class Matrix(
    override var a: Float = 1f,
    override var b: Float = 0f,
    override var c: Float = 0f,
    override var d: Float = 1f,
    override var tx: Float = 0f,
    override var ty: Float = 0f
) : MutableInterpolable<Matrix>, Interpolable<Matrix>, IMatrix {
    enum class Type(val id: Int) {
        IDENTITY(1),
        TRANSLATE(2),
        SCALE(3),
        SCALE_TRANSLATE(4),
        COMPLEX(5)
    }

    fun getType(): Type {
        val hasRotation = b != 0f || c != 0f
        val hasScale = a != 1f || b != 1f
        val hasTranslation = tx != 0f || ty != 0f

        return when {
            hasRotation -> Type.COMPLEX
            hasScale && hasTranslation -> Type.SCALE_TRANSLATE
            hasScale -> Type.SCALE
            hasTranslation -> Type.TRANSLATE
            else -> Type.IDENTITY
        }
    }

    data class Immutable(
        override val a: Float,
        override val b: Float,
        override val c: Float,
        override val d: Float,
        override val tx: Float,
        override val ty: Float
    ) : IMatrix {
        companion object {
            val IDENTITY = Immutable(1f, 0f, 0f, 1f, 0f, 0f)
        }

        fun toMutable() = Matrix(a, b, c, d, tx, ty)
    }

    fun toImmutable() = Immutable(a, b, c, d, tx, ty)

    fun setTo(a: Float, b: Float, c: Float, d: Float, tx: Float, ty: Float): Matrix = this.apply {
        this.a = a
        this.b = b
        this.c = c
        this.d = d
        this.tx = tx
        this.ty = ty
    }

    fun copyFrom(that: IMatrix): Matrix {
        setTo(that.a, that.b, that.c, that.d, that.tx, that.ty)
        return this
    }

    fun rotate(theta: Float) = this.apply {
        val cos = cos(theta)
        val sin = sin(theta)

        val a1 = a * cos - b * sin
        b = (a * sin + b * cos)
        a = a1

        val c1 = c * cos - d * sin
        d = (c * sin + d * cos)
        c = c1

        val tx1 = tx * cos - ty * sin
        ty = (tx * sin + ty * cos)
        tx = tx1
    }

    fun rotateDeg(thetaDeg: Float) = rotate(Angle.toRadians(thetaDeg))

    fun rotate(angle: Angle) = rotate(angle.radians)

    fun skew(skewX: Float, skewY: Float): Matrix {
        val sinX = sin(skewX)
        val cosX = cos(skewX)
        val sinY = sin(skewY)
        val cosY = cos(skewY)

        return this.setTo(
            a * cosY - b * sinX,
            a * sinY + b * cosX,
            c * cosY - d * sinX,
            c * sinY + d * cosX,
            tx * cosY - ty * sinX,
            tx * sinY + ty * cosX
        )
    }

    fun scale(sx: Float, sy: Float = sx) = setTo(a * sx, b * sx, c * sy, d * sy, tx * sx, ty * sy)
    fun prescale(sx: Float, sy: Float = sx) = setTo(a * sx, b * sx, c * sy, d * sy, tx, ty)
    fun translate(dx: Float, dy: Float) = this.apply { this.tx += dx; this.ty += dy }
    fun pretranslate(dx: Float, dy: Float) = this.apply { tx += a * dx + c * dy; ty += b * dx + d * dy }

    fun prerotate(theta: Float) = this.apply {
        val m = Matrix()
        m.rotate(theta)
        this.premultiply(m)
    }

    fun preskew(skewX: Float, skewY: Float) = this.apply {
        val m = Matrix()
        m.skew(skewX, skewY)
        this.premultiply(m)
    }

    fun premultiply(m: IMatrix) = this.premultiply(m.a, m.b, m.c, m.d, m.tx, m.ty)

    fun premultiply(la: Float, lb: Float, lc: Float, ld: Float, ltx: Float, lty: Float): Matrix = setTo(
        la * a + lb * c,
        la * b + lb * d,
        lc * a + ld * c,
        lc * b + ld * d,
        ltx * a + lty * c + tx,
        ltx * b + lty * d + ty
    )

    fun multiply(l: IMatrix, r: IMatrix): Matrix = setTo(
        l.a * r.a + l.b * r.c,
        l.a * r.b + l.b * r.d,
        l.c * r.a + l.d * r.c,
        l.c * r.b + l.d * r.d,
        l.tx * r.a + l.ty * r.c + r.tx,
        l.tx * r.b + l.ty * r.d + r.ty
    )

    fun transform(px: Float, py: Float, out: Point = Point()): Point =
        out.setTo(transformX(px, py), transformY(px, py))

    fun transform(p: IPoint, out: Point = Point()): Point =
        out.setTo(transformX(p.x, p.y), transformY(p.x, p.y))

    fun transformX(px: Float, py: Float): Float = (this.a * px + this.c * py + this.tx)
    fun transformY(px: Float, py: Float): Float = (this.d * py + this.b * px + this.ty)

    fun deltaTransformPoint(point: IPoint) = IPoint(point.x * a + point.y * c, point.x * b + point.y * d)

    override fun toString(): String = "Matrix(a=$a, b=$b, c=$c, d=$d, tx=$tx, ty=$ty)"

    fun identity() = setTo(1f, 0f, 0f, 1f, 0f, 0f)

    fun invert(matrixToInvert: IMatrix = this): Matrix {
        val src = matrixToInvert
        val dst = this
        val norm = src.a * src.d - src.b * src.c

        if (norm == 0f) {
            dst.setTo(0f, 0f, 0f, 0f, -src.tx, -src.ty)
        } else {
            val inorm = 1f / norm
            val d = src.a * inorm
            val a = src.d * inorm
            val b = src.b * -inorm
            val c = src.c * -inorm
            dst.setTo(a, b, c, d, -a * src.tx - c * src.ty, -b * src.tx - d * src.ty)
        }

        return this
    }

    fun inverted(out: Matrix = Matrix()) = out.invert(this)

    fun setTransform(
        x: Float,
        y: Float,
        scaleX: Float,
        scaleY: Float,
        rotation: Float,
        skewX: Float,
        skewY: Float
    ): Matrix {
        if (skewX == 0f && skewY == 0f) {
            if (rotation == 0f) {
                this.setTo(scaleX, 0f, 0f, scaleY, x, y)
            } else {
                val cos = cos(rotation)
                val sin = sin(rotation)
                this.setTo(cos * scaleX, sin * scaleY, -sin * scaleX, cos * scaleY, x, y)
            }
        } else {
            this.identity()
            scale(scaleX, scaleY)
            skew(skewX, skewY)
            rotate(rotation)
            translate(x, y)
        }
        return this
    }

    fun clone() = Matrix(a, b, c, d, tx, ty)

    fun createBox(scaleX: Float, scaleY: Float, rotation: Float = 0f, tx: Float = 0f, ty: Float = 0f): Unit {
        val u = cos(rotation)
        val v = sin(rotation)
        this.a = u * scaleX
        this.b = v * scaleY
        this.c = -v * scaleX
        this.d = u * scaleY
        this.tx = tx
        this.ty = ty
    }

    data class Transform(
        var x: Float = 0f, var y: Float = 0f,
        var scaleX: Float = 1f, var scaleY: Float = 1f,
        var skewX: Float = 0f, var skewY: Float = 0f,
        var rotation: Float = 0f
    ) {
        var rotationDegrees: Float
            get() = Angle.radiansToDegrees(rotation)
            set(value) = run { rotation = Angle.degreesToRadians(value) }

        fun setMatrix(matrix: IMatrix): Transform {
            val PI_4 = PI / 4.0
            this.x = matrix.tx
            this.y = matrix.ty

            this.skewX = atan(-matrix.c / matrix.d)
            this.skewY = atan(matrix.b / matrix.a)

            // Faster isNaN
            if (this.skewX != this.skewX) this.skewX = 0f
            if (this.skewY != this.skewY) this.skewY = 0f

            this.scaleY =
                if (this.skewX > -PI_4 && this.skewX < PI_4) matrix.d / cos(this.skewX) else -matrix.c / sin(this.skewX)
            this.scaleX =
                if (this.skewY > -PI_4 && this.skewY < PI_4) matrix.a / cos(this.skewY) else matrix.b / sin(this.skewY)

            if (abs(this.skewX - this.skewY) < 0.0001f) {
                this.rotation = this.skewX
                this.skewX = 0f
                this.skewY = 0f
            } else {
                this.rotation = 0f
            }

            return this
        }

        fun toMatrix(out: Matrix = Matrix()): Matrix = out.setTransform(x, y, scaleX, scaleY, rotation, skewX, skewY)
        fun copyFrom(that: Transform) = setTo(that.x, that.y, that.scaleX, that.scaleY, that.rotation, that.skewX, that.skewY)

        fun setTo(x: Float, y: Float, scaleX: Float, scaleY: Float, rotation: Float, skewX: Float, skewY: Float): Transform {
            this.x = x
            this.y = y
            this.scaleX = scaleX
            this.scaleY = scaleY
            this.rotation = rotation
            this.skewX = skewX
            this.skewY = skewY
            return this
        }

        inline fun setTo(x: Number, y: Number, scaleX: Number, scaleY: Number, rotation: Number, skewX: Number, skewY: Number): Transform =
            setTo(x.toFloat(), y.toFloat(), scaleX.toFloat(), scaleY.toFloat(), rotation.toFloat(), skewX.toFloat(), skewY.toFloat())

        fun clone() = Transform().copyFrom(this)
    }

    class Computed(val matrix: Matrix, val transform: Transform) {
        constructor(matrix: Matrix) : this(matrix, Transform().setMatrix(matrix))
        constructor(transform: Transform) : this(transform.toMatrix(), transform)
    }

    override fun setToInterpolated(l: Matrix, r: Matrix, ratio: Float) = this.setTo(
        a = ratio.interpolate(l.a, r.a),
        b = ratio.interpolate(l.b, r.b),
        c = ratio.interpolate(l.c, r.c),
        d = ratio.interpolate(l.d, r.d),
        tx = ratio.interpolate(l.tx, r.tx),
        ty = ratio.interpolate(l.ty, r.ty)
    )

    override fun interpolateWith(other: Matrix, ratio: Float): Matrix =
        Matrix().setToInterpolated(this, other, ratio)

    inline fun <T> keep(callback: Matrix.() -> T): T {
        val a = this.a
        val b = this.b
        val c = this.c
        val d = this.d
        val tx = this.tx
        val ty = this.ty
        try {
            return callback()
        } finally {
            this.a = a
            this.b = b
            this.c = c
            this.d = d
            this.tx = tx
            this.ty = ty
        }
    }
}

inline fun Matrix(a: Number, b: Number = 0.0, c: Number = 0.0, d: Number = 1.0, tx: Number = 0.0, ty: Number = 0.0) =
    Matrix(a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat(), tx.toFloat(), ty.toFloat())

fun Matrix(m: Matrix, out: Matrix = Matrix()): Matrix = out.copyFrom(m)

fun IMatrix.transformX(px: Float, py: Float): Float = this.a * px + this.c * py + this.tx
fun IMatrix.transformY(px: Float, py: Float): Float = this.d * py + this.b * px + this.ty

inline fun Matrix.setTo(a: Number, b: Number, c: Number, d: Number, tx: Number, ty: Number): Matrix = setTo(a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat(), tx.toFloat(), ty.toFloat())
inline fun Matrix.scale(sx: Number, sy: Number = sx) = scale(sx.toFloat(), sy.toFloat())
inline fun Matrix.prescale(sx: Number, sy: Number = sx) = prescale(sx.toFloat(), sy.toFloat())
inline fun Matrix.translate(dx: Number, dy: Number) = translate(dx.toFloat(), dy.toFloat())
inline fun Matrix.pretranslate(dx: Number, dy: Number) = pretranslate(dx.toFloat(), dy.toFloat())
inline fun Matrix.rotate(theta: Number) = rotate(theta.toFloat())
inline fun Matrix.skew(skewX: Number, skewY: Number): Matrix = skew(skewX.toFloat(), skewY.toFloat())
inline fun Matrix.prerotate(theta: Number) = prerotate(theta.toFloat())
inline fun Matrix.preskew(skewX: Number, skewY: Number) = preskew(skewX.toFloat(), skewY.toFloat())
inline fun Matrix.premultiply(la: Number, lb: Number, lc: Number, ld: Number, ltx: Number, lty: Number): Matrix = premultiply(la.toFloat(), lb.toFloat(), lc.toFloat(), ld.toFloat(), ltx.toFloat(), lty.toFloat())
inline fun Matrix.transformX(px: Number, py: Number): Float = transformX(px.toFloat(), py.toFloat())
inline fun Matrix.transformY(px: Number, py: Number): Float = transformY(px.toFloat(), py.toFloat())
inline fun Matrix.transformX(p: IPoint): Float = transformX(p.x, p.y)
inline fun Matrix.transformY(p: IPoint): Float = transformY(p.x, p.y)
inline fun Matrix.createBox(scaleX: Number, scaleY: Number, rotation: Number = 0f, tx: Number = 0f, ty: Number = 0f): Unit = createBox(scaleX.toFloat(), scaleY.toFloat(), rotation.toFloat(), tx.toFloat(), ty.toFloat())
inline fun Matrix.setTransform(x: Number, y: Number, scaleX: Number, scaleY: Number, rotation: Number, skewX: Number, skewY: Number): Matrix = setTransform(x.toFloat(), y.toFloat(), scaleX.toFloat(), scaleY.toFloat(), rotation.toFloat(), skewX.toFloat(), skewY.toFloat())

interface IMatrix3D {
    val data: FloatArray
}

fun IMatrix3D.index(x: Int, y: Int) = y * 4 + x
operator fun IMatrix3D.get(x: Int, y: Int): Float = data[index(x, y)]

fun IMatrix3D.getRow(n: Int, target: FloatArray = FloatArray(4)): FloatArray {
    val m = n * 4
    target[0] = data[m + 0]
    target[1] = data[m + 1]
    target[2] = data[m + 2]
    target[3] = data[m + 3]
    return target
}

fun IMatrix3D.getColumn(n: Int, target: FloatArray = FloatArray(4)): FloatArray {
    target[0] = data[n + 0]
    target[1] = data[n + 4]
    target[2] = data[n + 8]
    target[3] = data[n + 12]
    return target
}

class Matrix3D(
    override val data: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )
) : IMatrix3D {
    init {
        if (data.size != 16) error("Matrix3D data must be of size 16 (4x4)")
    }
    operator fun set(x: Int, y: Int, value: Float) = run { data[index(x, y)] = value }

    fun transpose(temp: Matrix3D = Matrix3D(), tempLine: FloatArray = FloatArray(4)): Matrix3D {
        temp.copyFrom(this)
        this.setRow(0, temp.getColumn(0, tempLine))
        this.setRow(1, temp.getColumn(1, tempLine))
        this.setRow(2, temp.getColumn(2, tempLine))
        this.setRow(3, temp.getColumn(3, tempLine))
        return this
    }

    fun setTo(
        a0: Float, b0: Float, c0: Float, d0: Float,
        a1: Float, b1: Float, c1: Float, d1: Float,
        a2: Float, b2: Float, c2: Float, d2: Float,
        a3: Float, b3: Float, c3: Float, d3: Float
    ): Matrix3D = this.apply {
        setRow(0, a0, b0, c0, d0)
        setRow(1, a1, b1, c1, d1)
        setRow(2, a2, b2, c2, d2)
        setRow(3, a3, b3, c3, d3)
    }

    fun setRow(n: Int, a: Float, b: Float, c: Float, d: Float): Matrix3D {
        val m = n * 4
        data[m + 0] = a
        data[m + 1] = b
        data[m + 2] = c
        data[m + 3] = d
        return this
    }

    fun setColumn(n: Int, a: Float, b: Float, c: Float, d: Float): Matrix3D {
        data[n + 0] = a
        data[n + 4] = b
        data[n + 8] = c
        data[n + 12] = d
        return this
    }

    fun setRow(n: Int, data: FloatArray): Matrix3D = setRow(n, data[0], data[1], data[2], data[3])
    fun setColumn(n: Int, data: FloatArray): Matrix3D = setColumn(n, data[0], data[1], data[2], data[3])

    fun identity() = this.apply {
        this.setTo(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )
    }

    fun multiply(l: Matrix3D, r: Matrix3D) = this.apply {
        multiply(data, l.data, r.data)
    }

    fun multiply(scale: Float) = this.apply {
        for (n in 0 until 16) this.data[n] *= scale
    }

    fun copyFrom(that: Matrix3D): Matrix3D {
        for (n in 0 until 16) this.data[n] = that.data[n]
        return this
    }

    fun setToOrtho(left: Float, top: Float, right: Float, bottom: Float, near: Float, far: Float): Matrix3D {
        val lr = 1 / (left - right)
        val bt = 1 / (bottom - top)
        val nf = 1 / (near - far)

        setRow(0, -2 * lr, 0f, 0f, 0f)
        setRow(1, 0f, -2 * bt, 0f, 0f)
        setRow(2, 0f, 0f, 2 * nf, 0f)
        setRow(3, (left + right) * lr, (top + bottom) * bt, (far + near) * nf, 1f)

        return this
    }

    override fun toString(): String = "Matrix3D(${data.toList()})"

    fun clone(): Matrix3D = Matrix3D(data.copyOf())

    companion object {
        operator fun invoke(vararg data: Float) = Matrix3D(data)

        fun multiply(out: FloatArray, a: FloatArray, b: FloatArray): FloatArray {
            val a00 = a[0]
            val a01 = a[1]
            val a02 = a[2]
            val a03 = a[3]
            val a10 = a[4]
            val a11 = a[5]
            val a12 = a[6]
            val a13 = a[7]
            val a20 = a[8]
            val a21 = a[9]
            val a22 = a[10]
            val a23 = a[11]
            val a30 = a[12]
            val a31 = a[13]
            val a32 = a[14]
            val a33 = a[15]

            // Cache only the current line of the second matrix
            var b0 = b[0]
            var b1 = b[1]
            var b2 = b[2]
            var b3 = b[3]
            out[0] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
            out[1] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
            out[2] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
            out[3] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

            b0 = b[4]; b1 = b[5]; b2 = b[6]; b3 = b[7]
            out[4] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
            out[5] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
            out[6] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
            out[7] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

            b0 = b[8]; b1 = b[9]; b2 = b[10]; b3 = b[11]
            out[8] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
            out[9] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
            out[10] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
            out[11] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

            b0 = b[12]; b1 = b[13]; b2 = b[14]; b3 = b[15]
            out[12] = b0 * a00 + b1 * a10 + b2 * a20 + b3 * a30
            out[13] = b0 * a01 + b1 * a11 + b2 * a21 + b3 * a31
            out[14] = b0 * a02 + b1 * a12 + b2 * a22 + b3 * a32
            out[15] = b0 * a03 + b1 * a13 + b2 * a23 + b3 * a33

            return out
        }
    }
}

fun Matrix3D.copyToFloatWxH(out: FloatArray, w: Int, h: Int) {
    var n = 0
    for (y in 0 until h) {
        val m = y * 4
        for (x in 0 until w) {
            out[n] = this.data[m + x]
            n++
        }
    }
}

fun Matrix3D.copyToFloat2x2(out: FloatArray) = copyToFloatWxH(out, 2, 2)
fun Matrix3D.copyToFloat3x3(out: FloatArray) = copyToFloatWxH(out, 3, 3)
fun Matrix3D.copyToFloat4x4(out: FloatArray) = run { for (n in 0 until 16) out[n] = this.data[n] }

inline fun Matrix3D.setToOrtho(left: Number, top: Number, right: Number, bottom: Number, near: Number, far: Number): Matrix3D = setToOrtho(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), near.toFloat(), far.toFloat())
inline fun Matrix3D.setTo(
    a0: Number, b0: Number, c0: Number, d0: Number,
    a1: Number, b1: Number, c1: Number, d1: Number,
    a2: Number, b2: Number, c2: Number, d2: Number,
    a3: Number, b3: Number, c3: Number, d3: Number
): Matrix3D = setTo(
    a0.toFloat(), b0.toFloat(), c0.toFloat(), d0.toFloat(),
    a1.toFloat(), b1.toFloat(), c1.toFloat(), d1.toFloat(),
    a2.toFloat(), b2.toFloat(), c2.toFloat(), d2.toFloat(),
    a3.toFloat(), b3.toFloat(), c3.toFloat(), d3.toFloat()
)

inline fun Matrix3D.setRow(n: Int, a: Number, b: Number, c: Number, d: Number): Matrix3D = setRow(n, a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat())
inline fun Matrix3D.setColumn(n: Int, a: Number, b: Number, c: Number, d: Number): Matrix3D = setColumn(n, a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat())

fun Matrix.toMatrix3D(out: Matrix3D = Matrix3D()): Matrix3D = out.copyFrom(this)

fun Matrix3D.copyFrom(that: Matrix): Matrix3D = setTo(
    that.a, that.b, that.tx, 0f,
    that.c, that.d, that.ty, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)

inline operator fun Matrix3D.times(that: Matrix3D): Matrix3D = Matrix3D().multiply(this, that)
inline operator fun Matrix3D.times(value: Number): Matrix3D = Matrix3D().multiply(value.toFloat())
inline operator fun Matrix3D.div(value: Number): Matrix3D = Matrix3D().multiply(1f / value.toFloat())
