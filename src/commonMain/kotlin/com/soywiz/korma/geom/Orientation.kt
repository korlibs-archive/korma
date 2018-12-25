package com.soywiz.korma.geom

enum class Orientation(val value: Int) {
    CW(+1), CCW(-1), COLLINEAR(0);

    companion object {
        private const val EPSILON: Float = 1e-12f

        fun orient2d(pa: IPoint, pb: IPoint, pc: IPoint): Orientation {
            val detleft: Float = (pa.x - pc.x) * (pb.y - pc.y)
            val detright: Float = (pa.y - pc.y) * (pb.x - pc.x)
            val `val`: Float = detleft - detright

            if ((`val` > -EPSILON) && (`val` < EPSILON)) return Orientation.COLLINEAR
            if (`val` > 0) return Orientation.CCW
            return Orientation.CW
        }
    }
}
