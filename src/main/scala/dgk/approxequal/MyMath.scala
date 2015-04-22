package dgk.approxequal

object Helpers {
  final val EPS = 1e-3
}

// A trivial 2D vector implementation.
case class Vec2D(x: Double, y: Double) {
  def euclideanDistance(that: Vec2D): Double = {
    val dX = x - that.x
    val dY = y - that.y
    math.sqrt(dX * dX + dY * dY)
  }

  def manhattanDistance(that: Vec2D): Double = {
    val dX = math.abs(x - that.x)
    val dY = math.abs(y - that.y)
    dX + dY
  }
}
