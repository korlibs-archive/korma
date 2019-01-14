package com.soywiz.korma.geom

import kotlin.math.*

enum class MajorOrder { ROW, COLUMN }

// Stored as four consecutive column vectors (effectively stored in column-major order) see https://en.wikipedia.org/wiki/Row-_and_column-major_order
class Matrix3D {
    val data: FloatArray = floatArrayOf(
        1f, 0f, 0f, 0f, // column-0
        0f, 1f, 0f, 0f, // column-1
        0f, 0f, 1f, 0f, // column-2
        0f, 0f, 0f, 1f  // column-3
    )

    companion object {
        const val M00 = 0
        const val M10 = 1
        const val M20 = 2
        const val M30 = 3

        const val M01 = 4
        const val M11 = 5
        const val M21 = 6
        const val M31 = 7

        const val M02 = 8
        const val M12 = 9
        const val M22 = 10
        const val M32 = 11

        const val M03 = 12
        const val M13 = 13
        const val M23 = 14
        const val M33 = 15

        operator fun invoke(m: Matrix3D) = Matrix3D().copyFrom(m)

        fun fromRows(
            a00: Float, a01: Float, a02: Float, a03: Float,
            a10: Float, a11: Float, a12: Float, a13: Float,
            a20: Float, a21: Float, a22: Float, a23: Float,
            a30: Float, a31: Float, a32: Float, a33: Float
        ) = Matrix3D().setRows(
            a00, a01, a02, a03,
            a10, a11, a12, a13,
            a20, a21, a22, a23,
            a30, a31, a32, a33
        )

        fun fromColumns(
            a00: Float, a10: Float, a20: Float, a30: Float,
            a01: Float, a11: Float, a21: Float, a31: Float,
            a02: Float, a12: Float, a22: Float, a32: Float,
            a03: Float, a13: Float, a23: Float, a33: Float
        ) = Matrix3D().setColumns(
            a00, a10, a20, a30,
            a01, a11, a21, a31,
            a02, a12, a22, a32,
            a03, a13, a23, a33
        )

        fun rowMajorIndex(row: Int, column: Int) = row * 4 + column
        fun columnMajorIndex(row: Int, column: Int) = column * 4 + row
        fun index(row: Int, column: Int, order: MajorOrder) = if (order == MajorOrder.ROW) rowMajorIndex(row, column) else columnMajorIndex(row, column)

        fun multiply(left: FloatArray, right: FloatArray, out: FloatArray = FloatArray(16)): FloatArray {
            for (row in 0 until 4) {
                for (column in 0 until 4) {
                    var value = 0f
                    for (n in 0 until 4) {
                        value += left[columnMajorIndex(row, n)] * right[columnMajorIndex(n, column)]
                    }
                    out[columnMajorIndex(row, column)] = value
                }
            }
            return out
        }
    }

    operator fun get(row: Int, column: Int): Float = data[columnMajorIndex(row, column)]
    operator fun set(row: Int, column: Int, value: Float) = run { data[columnMajorIndex(row, column)] = value }
    inline operator fun set(row: Int, column: Int, value: Number) = set(row, column, value.toFloat())

    inline var v00: Float get() = data[M00]; set(v) = run { data[M00] = v }
    inline var v01: Float get() = data[M01]; set(v) = run { data[M01] = v }
    inline var v02: Float get() = data[M02]; set(v) = run { data[M02] = v }
    inline var v03: Float get() = data[M03]; set(v) = run { data[M03] = v }

    inline var v10: Float get() = data[M10]; set(v) = run { data[M10] = v }
    inline var v11: Float get() = data[M11]; set(v) = run { data[M11] = v }
    inline var v12: Float get() = data[M12]; set(v) = run { data[M12] = v }
    inline var v13: Float get() = data[M13]; set(v) = run { data[M13] = v }

    inline var v20: Float get() = data[M20]; set(v) = run { data[M20] = v }
    inline var v21: Float get() = data[M21]; set(v) = run { data[M21] = v }
    inline var v22: Float get() = data[M22]; set(v) = run { data[M22] = v }
    inline var v23: Float get() = data[M23]; set(v) = run { data[M23] = v }

    inline var v30: Float get() = data[M30]; set(v) = run { data[M30] = v }
    inline var v31: Float get() = data[M31]; set(v) = run { data[M31] = v }
    inline var v32: Float get() = data[M32]; set(v) = run { data[M32] = v }
    inline var v33: Float get() = data[M33]; set(v) = run { data[M33] = v }

    val transposed: Matrix3D get() = this.clone().transpose()

    fun transpose(): Matrix3D = setColumns(
        v00, v01, v02, v03,
        v10, v11, v12, v13,
        v20, v21, v22, v23,
        v30, v31, v32, v33
    )

    fun setRows(
        a00: Float, a01: Float, a02: Float, a03: Float,
        a10: Float, a11: Float, a12: Float, a13: Float,
        a20: Float, a21: Float, a22: Float, a23: Float,
        a30: Float, a31: Float, a32: Float, a33: Float
    ): Matrix3D = this.apply {
        v00 = a00; v01 = a01; v02 = a02; v03 = a03
        v10 = a10; v11 = a11; v12 = a12; v13 = a13
        v20 = a20; v21 = a21; v22 = a22; v23 = a23
        v30 = a30; v31 = a31; v32 = a32; v33 = a33
    }

    fun setColumns(
        a00: Float, a10: Float, a20: Float, a30: Float,
        a01: Float, a11: Float, a21: Float, a31: Float,
        a02: Float, a12: Float, a22: Float, a32: Float,
        a03: Float, a13: Float, a23: Float, a33: Float
    ): Matrix3D = this.apply {
        v00 = a00; v01 = a01; v02 = a02; v03 = a03
        v10 = a10; v11 = a11; v12 = a12; v13 = a13
        v20 = a20; v21 = a21; v22 = a22; v23 = a23
        v30 = a30; v31 = a31; v32 = a32; v33 = a33
    }

    fun setRow(row: Int, a: Float, b: Float, c: Float, d: Float): Matrix3D {
        data[columnMajorIndex(row, 0)] = a
        data[columnMajorIndex(row, 1)] = b
        data[columnMajorIndex(row, 2)] = c
        data[columnMajorIndex(row, 3)] = d
        return this
    }

    fun setColumn(column: Int, a: Float, b: Float, c: Float, d: Float): Matrix3D {
        data[columnMajorIndex(0, column)] = a
        data[columnMajorIndex(1, column)] = b
        data[columnMajorIndex(2, column)] = c
        data[columnMajorIndex(3, column)] = d
        return this
    }

    fun getRow(n: Int, target: FloatArray = FloatArray(4)): FloatArray {
        val m = n * 4
        target[0] = data[m + 0]
        target[1] = data[m + 1]
        target[2] = data[m + 2]
        target[3] = data[m + 3]
        return target
    }

    fun getColumn(n: Int, target: FloatArray = FloatArray(4)): FloatArray {
        target[0] = data[n + 0]
        target[1] = data[n + 4]
        target[2] = data[n + 8]
        target[3] = data[n + 12]
        return target
    }

    fun setRow(row: Int, data: FloatArray): Matrix3D = setRow(row, data[0], data[1], data[2], data[3])
    fun setColumn(column: Int, data: FloatArray): Matrix3D = setColumn(column, data[0], data[1], data[2], data[3])

    fun setRow(row: Int, data: Vector3D): Matrix3D = setRow(row, data.x, data.y, data.w, data.z)
    fun setColumn(column: Int, data: Vector3D): Matrix3D = setColumn(column, data.x, data.y, data.w, data.z)

    inline fun setRow(row: Int, a: Number, b: Number, c: Number, d: Number): Matrix3D = setRow(row, a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat())
    inline fun setColumn(column: Int, a: Number, b: Number, c: Number, d: Number): Matrix3D = setColumn(column, a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat())

    fun identity() = this.setColumns(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    fun multiply(l: Matrix3D, r: Matrix3D) = this.setRows(
        (l.v00 * r.v00) + (l.v01 * r.v10) + (l.v02 * r.v20) + (l.v03 * r.v30),
        (l.v00 * r.v01) + (l.v01 * r.v11) + (l.v02 * r.v21) + (l.v03 * r.v31),
        (l.v00 * r.v02) + (l.v01 * r.v12) + (l.v02 * r.v22) + (l.v03 * r.v32),
        (l.v00 * r.v03) + (l.v01 * r.v13) + (l.v02 * r.v23) + (l.v03 * r.v33),

        (l.v10 * r.v00) + (l.v11 * r.v10) + (l.v12 * r.v20) + (l.v13 * r.v30),
        (l.v10 * r.v01) + (l.v11 * r.v11) + (l.v12 * r.v21) + (l.v13 * r.v31),
        (l.v10 * r.v02) + (l.v11 * r.v12) + (l.v12 * r.v22) + (l.v13 * r.v32),
        (l.v10 * r.v03) + (l.v11 * r.v13) + (l.v12 * r.v23) + (l.v13 * r.v33),

        (l.v20 * r.v00) + (l.v21 * r.v10) + (l.v22 * r.v20) + (l.v23 * r.v30),
        (l.v20 * r.v01) + (l.v21 * r.v11) + (l.v22 * r.v21) + (l.v23 * r.v31),
        (l.v20 * r.v02) + (l.v21 * r.v12) + (l.v22 * r.v22) + (l.v23 * r.v32),
        (l.v20 * r.v03) + (l.v21 * r.v13) + (l.v22 * r.v23) + (l.v23 * r.v33),

        (l.v30 * r.v00) + (l.v31 * r.v10) + (l.v32 * r.v20) + (l.v33 * r.v30),
        (l.v30 * r.v01) + (l.v31 * r.v11) + (l.v32 * r.v21) + (l.v33 * r.v31),
        (l.v30 * r.v02) + (l.v31 * r.v12) + (l.v32 * r.v22) + (l.v33 * r.v32),
        (l.v30 * r.v03) + (l.v31 * r.v13) + (l.v32 * r.v23) + (l.v33 * r.v33)
    )

    fun multiply(scale: Float) = this.apply {
        for (n in 0 until 16) this.data[n] *= scale
    }

    fun copyFrom(that: Matrix3D): Matrix3D {
        for (n in 0 until 16) this.data[n] = that.data[n]
        return this
    }

    fun transform(x: Float, y: Float, z: Float, w: Float = 1f, out: Vector3D = Vector3D(0, 0, 0, 0)): Vector3D = out.setTo(
        (v00 * x) + (v01 * y) + (v02 * z) + (v03 * w),
        (v10 * x) + (v11 * y) + (v12 * z) + (v13 * w),
        (v20 * x) + (v21 * y) + (v22 * z) + (v23 * w),
        (v30 * x) + (v31 * y) + (v32 * z) + (v33 * w)
    )

    fun transform(v: Vector3D, out: Vector3D = Vector3D()): Vector3D = transform(v.x, v.y, v.z, v.w, out)

    fun setToOrtho(left: Float, top: Float, right: Float, bottom: Float, near: Float, far: Float): Matrix3D {
        val lr = 1f / (left - right)
        val bt = 1f / (bottom - top)
        val nf = 1f / (near - far)

        return setRows(
            -2f * lr, 0f, 0f, 0f,
            0f, -2f * bt, 0f, 0f,
            0f, 0f, 2f * nf, 0f,
            (left + right) * lr, (top + bottom) * bt, (far + near) * nf, 1f
        )
    }

    override fun equals(other: Any?): Boolean = (other is Matrix3D) && this.data.contentEquals(other.data)
    override fun hashCode(): Int = data.contentHashCode()

    override fun toString(): String = buildString {
        append("Matrix3D(\n")
        for (row in 0 until 4) {
            append("  [ ")
            for (col in 0 until 4) {
                if (col != 0) append(", ")
                val v = get(row, col)
                if (floor(v) == v) append(v.toInt()) else append(v)
            }
            append(" ],\n")
        }
        append(")")
    }

    fun clone(): Matrix3D = Matrix3D().copyFrom(this)
}

fun Matrix3D.copyToFloatWxH(out: FloatArray, rows: Int, columns: Int, order: MajorOrder) {
    var n = 0
    if (order == MajorOrder.ROW) {
        for (column in 0 until columns) for (row in 0 until rows) out[n++] = data[Matrix3D.rowMajorIndex(row, column)]
    } else {
        for (column in 0 until columns) for (row in 0 until rows) out[n++] = data[Matrix3D.columnMajorIndex(row, column)]
    }
}

fun Matrix3D.copyToFloat2x2(out: FloatArray, order: MajorOrder) = copyToFloatWxH(out, 2, 2, order)
fun Matrix3D.copyToFloat3x3(out: FloatArray, order: MajorOrder) = copyToFloatWxH(out, 3, 3, order)
fun Matrix3D.copyToFloat4x4(out: FloatArray, order: MajorOrder) = copyToFloatWxH(out, 4, 4, order)

inline fun Matrix3D.setToOrtho(left: Number, top: Number, right: Number, bottom: Number, near: Number, far: Number): Matrix3D = setToOrtho(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), near.toFloat(), far.toFloat())

inline fun Matrix3D.setRows(
    a00: Number, a01: Number, a02: Number, a03: Number,
    a10: Number, a11: Number, a12: Number, a13: Number,
    a20: Number, a21: Number, a22: Number, a23: Number,
    a30: Number, a31: Number, a32: Number, a33: Number
): Matrix3D = setRows(
    a00.toFloat(), a01.toFloat(), a02.toFloat(), a03.toFloat(),
    a10.toFloat(), a11.toFloat(), a12.toFloat(), a13.toFloat(),
    a20.toFloat(), a21.toFloat(), a22.toFloat(), a23.toFloat(),
    a30.toFloat(), a31.toFloat(), a32.toFloat(), a33.toFloat()
)

inline fun Matrix3D.setColumns(
    a00: Number, a10: Number, a20: Number, a30: Number,
    a01: Number, a11: Number, a21: Number, a31: Number,
    a02: Number, a12: Number, a22: Number, a32: Number,
    a03: Number, a13: Number, a23: Number, a33: Number
): Matrix3D = setColumns(
    a00.toFloat(), a10.toFloat(), a20.toFloat(), a30.toFloat(),
    a01.toFloat(), a11.toFloat(), a21.toFloat(), a31.toFloat(),
    a02.toFloat(), a12.toFloat(), a22.toFloat(), a32.toFloat(),
    a03.toFloat(), a13.toFloat(), a23.toFloat(), a33.toFloat()
)

inline fun Matrix3D.Companion.fromRows(
    a00: Number, a01: Number, a02: Number, a03: Number,
    a10: Number, a11: Number, a12: Number, a13: Number,
    a20: Number, a21: Number, a22: Number, a23: Number,
    a30: Number, a31: Number, a32: Number, a33: Number
): Matrix3D = fromRows(
    a00.toFloat(), a01.toFloat(), a02.toFloat(), a03.toFloat(),
    a10.toFloat(), a11.toFloat(), a12.toFloat(), a13.toFloat(),
    a20.toFloat(), a21.toFloat(), a22.toFloat(), a23.toFloat(),
    a30.toFloat(), a31.toFloat(), a32.toFloat(), a33.toFloat()
)

inline fun Matrix3D.Companion.fromColumns(
    a00: Number, a10: Number, a20: Number, a30: Number,
    a01: Number, a11: Number, a21: Number, a31: Number,
    a02: Number, a12: Number, a22: Number, a32: Number,
    a03: Number, a13: Number, a23: Number, a33: Number
): Matrix3D = fromColumns(
    a00.toFloat(), a10.toFloat(), a20.toFloat(), a30.toFloat(),
    a01.toFloat(), a11.toFloat(), a21.toFloat(), a31.toFloat(),
    a02.toFloat(), a12.toFloat(), a22.toFloat(), a32.toFloat(),
    a03.toFloat(), a13.toFloat(), a23.toFloat(), a33.toFloat()
)


inline operator fun Matrix3D.times(that: Matrix3D): Matrix3D = Matrix3D().multiply(this, that)
inline operator fun Matrix3D.times(value: Number): Matrix3D = Matrix3D(this).multiply(value.toFloat())
inline operator fun Matrix3D.div(value: Number): Matrix3D = Matrix3D(this).multiply(1f / value.toFloat())
inline fun Matrix3D.multiply(scale: Number) = multiply(scale.toFloat())
