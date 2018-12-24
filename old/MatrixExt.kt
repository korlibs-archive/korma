fun Matrix2d.createGradientBox(
    width: Double,
    height: Double,
    rotation: Double = 0.0,
    tx: Double = 0.0,
    ty: Double = 0.0
): Unit {
    this.createBox(width / 1638.4, height / 1638.4, rotation, tx + width / 2, ty + height / 2)
}
