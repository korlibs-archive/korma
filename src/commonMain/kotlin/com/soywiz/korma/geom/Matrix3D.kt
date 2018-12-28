package com.soywiz.korma.geom

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

    companion object {
        operator fun invoke(m: Matrix3D) = Matrix3D(m.data.copyOf())

        operator fun invoke(
            a0: Float, b0: Float, c0: Float, d0: Float,
            a1: Float, b1: Float, c1: Float, d1: Float,
            a2: Float, b2: Float, c2: Float, d2: Float,
            a3: Float, b3: Float, c3: Float, d3: Float
        ) = Matrix3D(floatArrayOf(
            a0, b0, c0, d0,
            a1, b1, c1, d1,
            a2, b2, c2, d2,
            a3, b3, c3, d3
        ))

        operator fun invoke(
            a0: Float, b0: Float, c0: Float,
            a1: Float, b1: Float, c1: Float,
            a2: Float, b2: Float, c2: Float
        ) = Matrix3D(floatArrayOf(
            a0, b0, c0, 0f,
            a1, b1, c1, 0f,
            a2, b2, c2, 0f,
            0f, 0f, 0f, 1f
        ))

        inline operator fun invoke(
            a0: Number, b0: Number, c0: Number, d0: Number,
            a1: Number, b1: Number, c1: Number, d1: Number,
            a2: Number, b2: Number, c2: Number, d2: Number,
            a3: Number, b3: Number, c3: Number, d3: Number
        ) = Matrix3D(floatArrayOf(
            a0.toFloat(), b0.toFloat(), c0.toFloat(), d0.toFloat(),
            a1.toFloat(), b1.toFloat(), c1.toFloat(), d1.toFloat(),
            a2.toFloat(), b2.toFloat(), c2.toFloat(), d2.toFloat(),
            a3.toFloat(), b3.toFloat(), c3.toFloat(), d3.toFloat()
        ))

        inline operator fun invoke(
            a0: Number, b0: Number, c0: Number,
            a1: Number, b1: Number, c1: Number,
            a2: Number, b2: Number, c2: Number
        ) = Matrix3D(floatArrayOf(
            a0.toFloat(), b0.toFloat(), c0.toFloat(), 0f,
            a1.toFloat(), b1.toFloat(), c1.toFloat(), 0f,
            a2.toFloat(), b2.toFloat(), c2.toFloat(), 0f,
            0f, 0f, 0f, 1f
        ))

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

    operator fun set(x: Int, y: Int, value: Float) = run { data[index(x, y)] = value }
    inline operator fun set(x: Int, y: Int, value: Number) = set(x, y, value.toFloat())

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

    fun transform(v: Vector3D, out: Vector3D = Vector3D(0, 0, 0, 0)): Vector3D {
        val row1 = getRow(0)
        val row2 = getRow(1)
        val row3 = getRow(2)
        val row4 = getRow(3)
        out.setTo(
            row1[0]*v.x + row2[0]*v.y + row3[0]*v.z + row4[0]*v.w,
            row1[1]*v.x + row2[1]*v.y + row3[1]*v.z + row4[1]*v.w,
            row1[2]*v.x + row2[2]*v.y + row3[2]*v.z + row4[2]*v.w,
            row1[3]*v.x + row2[3]*v.y + row3[3]*v.z + row4[3]*v.w
        )
        return out
    }

    fun setToOrtho(left: Float, top: Float, right: Float, bottom: Float, near: Float, far: Float): Matrix3D {
        val lr = 1f / (left - right)
        val bt = 1f / (bottom - top)
        val nf = 1f / (near - far)

        setTo(
            -2f * lr, 0f, 0f, 0f,
            0f, -2f * bt, 0f, 0f,
            0f, 0f, 2f * nf, 0f,
            (left + right) * lr, (top + bottom) * bt, (far + near) * nf, 1f
        )

        return this
    }

    override fun toString(): String = "Matrix3D(${data.toList()})"

    fun clone(): Matrix3D = Matrix3D(data.copyOf())
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
fun Matrix3D.copyToFloat4x4(out: FloatArray) = copyToFloatWxH(out, 4, 4)

fun Matrix3D.copyToDoubleWxH(out: DoubleArray, w: Int, h: Int) {
    var n = 0
    for (y in 0 until h) {
        val m = y * 4
        for (x in 0 until w) {
            out[n] = this.data[m + x].toDouble()
            n++
        }
    }
}

fun Matrix3D.copyToDouble2x2(out: DoubleArray) = copyToDoubleWxH(out, 2, 2)
fun Matrix3D.copyToDouble3x3(out: DoubleArray) = copyToDoubleWxH(out, 3, 3)
fun Matrix3D.copyToDouble4x4(out: DoubleArray) = copyToDoubleWxH(out, 4, 4)

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

inline operator fun Matrix3D.times(that: Matrix3D): Matrix3D = Matrix3D().multiply(this, that)
inline operator fun Matrix3D.times(value: Number): Matrix3D = Matrix3D(this).multiply(value.toFloat())
inline operator fun Matrix3D.div(value: Number): Matrix3D = Matrix3D(this).multiply(1f / value.toFloat())
inline fun Matrix3D.multiply(scale: Number) = multiply(scale.toFloat())
