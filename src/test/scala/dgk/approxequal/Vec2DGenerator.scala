package dgk.approxequal

import org.scalacheck.Arbitrary

object Vec2DGenerator {
  implicit lazy val arbVec2D: Arbitrary[Vec2D] = Arbitrary {
    for {
      x <- Arbitrary.arbitrary[Double]
      y <- Arbitrary.arbitrary[Double]
    } yield Vec2D(x, y)
  }
}
