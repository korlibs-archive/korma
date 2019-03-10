package com.soywiz.korma.geom

import kotlin.math.*

data class EulerRotation(
    var x: Angle = 0.degrees,
    var y: Angle = 0.degrees,
    var z: Angle = 0.degrees
)

data class Quaternion(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var w: Double = 1.0
)

fun Quaternion(x: Number, y: Number, z: Number, w: Number) = Quaternion(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

fun EulerRotation.setQuaternion(x: Number, y: Number, z: Number, w: Number): EulerRotation = quaternionToEuler(x, y, z, w, this)
fun EulerRotation.setQuaternion(quaternion: Quaternion): EulerRotation = quaternionToEuler(quaternion.x, quaternion.y, quaternion.z, quaternion.w, this)
fun EulerRotation.setTo(x: Angle, y: Angle, z: Angle): EulerRotation = this
    .apply { this.x = x }
    .apply { this.y = y }
    .apply { this.z = z }

fun EulerRotation.setTo(other: EulerRotation): EulerRotation = setTo(other.x, other.y, other.z)

fun Quaternion.setEuler(x: Angle, y: Angle, z: Angle): Quaternion = eulerToQuaternion(x, y, z, this)
fun Quaternion.setEuler(euler: EulerRotation): Quaternion = eulerToQuaternion(euler, this)
fun Quaternion.setTo(euler: EulerRotation): Quaternion = eulerToQuaternion(euler, this)
inline fun Quaternion.setTo(x: Number, y: Number, z: Number, w: Number): Quaternion = this
    .apply { this.x = x.toDouble() }
    .apply { this.y = y.toDouble() }
    .apply { this.z = z.toDouble() }
    .apply { this.w = w.toDouble() }

inline fun Quaternion.copyFrom(other: Quaternion): Quaternion = this.setTo(other)

inline fun Quaternion.setTo(other: Quaternion): Quaternion = setTo(other.x, other.y, other.z, other.w)

private val tempQuat = Quaternion()
fun EulerRotation.toMatrix(out: Matrix3D = Matrix3D()): Matrix3D = tempQuat.setEuler(this).toMatrix(out)
fun Quaternion.toMatrix(out: Matrix3D = Matrix3D()): Matrix3D = quaternionToMatrix(this, out)

fun eulerToQuaternion(euler: EulerRotation, quaternion: Quaternion = Quaternion()): Quaternion = eulerToQuaternion(euler.x, euler.y, euler.z, quaternion)

fun quaternionToEuler(q: Quaternion, euler: EulerRotation = EulerRotation()): EulerRotation = quaternionToEuler(q.x, q.y, q.z, q.w, euler)

inline fun quaternionToEuler(x: Number, y: Number, z: Number, w: Number, euler: EulerRotation = EulerRotation()): EulerRotation {
    return quaternionToEuler(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat(), euler)
}

// https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles

fun eulerToQuaternion(roll: Angle, pitch: Angle, yaw: Angle, quaternion: Quaternion = Quaternion()): Quaternion {
    val cr = cos(roll * 0.5)
    val sr = sin(roll * 0.5)
    val cp = cos(pitch * 0.5)
    val sp = sin(pitch * 0.5)
    val cy = cos(yaw * 0.5)
    val sy = sin(yaw * 0.5)
    return quaternion.setTo(
        (cy * cp * sr - sy * sp * cr),
        (sy * cp * sr + cy * sp * cr),
        (sy * cp * cr - cy * sp * sr),
        (cy * cp * cr + sy * sp * sr)
    )
}

fun quaternionToEuler(x: Float, y: Float, z: Float, w: Float, euler: EulerRotation = EulerRotation()): EulerRotation {
    val sinrCosp = +2.0 * (w * x + y * z)
    val cosrCosp = +1.0 - 2.0 * (x * x + y * y)
    val roll = atan2(sinrCosp, cosrCosp)
    val sinp = +2.0 * (w * y - z * x)
    val pitch = when {
        abs(sinp) >= 1 -> if (sinp > 0) PI / 2 else -PI / 2
        else -> asin(sinp)
    }
    val sinyCosp = +2.0 * (w * z + x * y)
    val cosyCosp = +1.0 - 2.0 * (y * y + z * z)
    val yaw = atan2(sinyCosp, cosyCosp)
    return euler.setTo(roll.radians, pitch.radians, yaw.radians)
}

private val tempMat1 = Matrix3D()
private val tempMat2 = Matrix3D()
fun quaternionToMatrix(quat: Quaternion, out: Matrix3D = Matrix3D(), temp1: Matrix3D = tempMat1, temp2: Matrix3D = tempMat2): Matrix3D {
    temp1.setRows(
        quat.w, quat.z, -quat.y, quat.x,
        -quat.z, quat.w, quat.x, quat.y,
        quat.y, -quat.x, quat.w, quat.z,
        -quat.x, -quat.y, -quat.z, quat.w
    )
    temp2.setRows(
        quat.w, quat.z, -quat.y, -quat.x,
        -quat.z, quat.w, quat.x, -quat.y,
        quat.y, -quat.x, quat.w, -quat.z,
        quat.x, quat.y, quat.z, quat.w
    )
    return out.multiply(temp1, temp2)
}

fun Quaternion.setFromRotationMatrix(m: Matrix3D) = this.apply {
    val q = this
    m.apply {
        val t = v00 + v11 + v22
        when {
            t > 0 -> {
                val s = 0.5 / sqrt(t + 1.0)
                q.setTo(((v21 - v12) * s), ((v02 - v20) * s), ((v10 - v01) * s), (0.25 / s))
            }
            v00 > v11 && v00 > v22 -> {
                val s = 2.0 * sqrt(1.0 + v00 - v11 - v22)
                q.setTo((0.25 * s), ((v01 + v10) / s), ((v02 + v20) / s), ((v21 - v12) / s))
            }
            v11 > v22 -> {
                val s = 2.0 * sqrt(1.0 + v11 - v00 - v22)
                q.setTo(((v01 + v10) / s), (0.25 * s), ((v12 + v21) / s), ((v02 - v20) / s))
            }
            else -> {
                val s = 2.0 * sqrt(1.0 + v22 - v00 - v11)
                q.setTo(((v02 + v20) / s), ((v12 + v21) / s), (0.25f * s), ((v10 - v01) / s))
            }
        }
    }
}
