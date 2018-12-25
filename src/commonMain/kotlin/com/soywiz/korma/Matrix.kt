package com.soywiz.korma

import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.MPoint2d
import com.soywiz.korma.geom.Point2d
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

    fun rotateDeg(thetaDeg: Float) = rotate(Angle.toRadians(thetaDeg.toDouble()).toFloat())

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
        val m = com.soywiz.korma.Matrix()
        m.rotate(theta)
        this.premultiply(m)
    }

    fun preskew(skewX: Float, skewY: Float) = this.apply {
        val m = com.soywiz.korma.Matrix()
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

    fun transform(px: Double, py: Double, out: MVector2 = MVector2()): MPoint2d =
        out.setTo(transformX(px, py), transformY(px, py))

    fun transform(p: Vector2, out: MVector2 = MVector2()): MPoint2d =
        out.setTo(transformX(p.x, p.y), transformY(p.x, p.y))

    fun transformX(px: Double, py: Double): Double = this.a * px + this.c * py + this.tx
    fun transformY(px: Double, py: Double): Double = this.d * py + this.b * px + this.ty

    fun transformXf(px: Double, py: Double): Float = (this.a * px + this.c * py + this.tx).toFloat()
    fun transformYf(px: Double, py: Double): Float = (this.d * py + this.b * px + this.ty).toFloat()

    fun deltaTransformPoint(point: Vector2) = Vector2(point.x * a + point.y * c, point.x * b + point.y * d)

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

    override fun setToInterpolated(l: Matrix, r: Matrix, ratio: Double) = this.setTo(
        a = ratio.interpolate(l.a, r.a),
        b = ratio.interpolate(l.b, r.b),
        c = ratio.interpolate(l.c, r.c),
        d = ratio.interpolate(l.d, r.d),
        tx = ratio.interpolate(l.tx, r.tx),
        ty = ratio.interpolate(l.ty, r.ty)
    )

    override fun interpolateWith(other: Matrix, ratio: Double): Matrix =
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

fun IMatrix.transformX(px: Double, py: Double): Double = this.a * px + this.c * py + this.tx
fun IMatrix.transformY(px: Double, py: Double): Double = this.d * py + this.b * px + this.ty


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
inline fun Matrix.transformX(px: Number, py: Number): Double = transformX(px.toDouble(), py.toDouble())
inline fun Matrix.transformY(px: Number, py: Number): Double = transformY(px.toDouble(), py.toDouble())
inline fun Matrix.transformX(p: Point2d): Double = transformX(p.x, p.y)
inline fun Matrix.transformY(p: Point2d): Double = transformY(p.x, p.y)
inline fun Matrix.createBox(scaleX: Number, scaleY: Number, rotation: Number = 0f, tx: Number = 0f, ty: Number = 0f): Unit = createBox(scaleX.toFloat(), scaleY.toFloat(), rotation.toFloat(), tx.toFloat(), ty.toFloat())
inline fun Matrix.setTransform(x: Number, y: Number, scaleX: Number, scaleY: Number, rotation: Number, skewX: Number, skewY: Number): Matrix = setTransform(x.toFloat(), y.toFloat(), scaleX.toFloat(), scaleY.toFloat(), rotation.toFloat(), skewX.toFloat(), skewY.toFloat())
