package dgk.approxequal

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AnotherApproxTCTest extends CheckTCLaws {
  import Vec2DGenerator._
  import AnotherVec2DWithDistanceTC._
  import Vec2DApproxEqualTC._
  import ProductApproxEqualTC._

  // NOTE: Here we test the alternative Vec2D distance metric.
  checkLaws[Vec2D]("AnotherVec2D")
  checkLaws[(Vec2D, Vec2D)]("AnotherVec2DPair")
}
