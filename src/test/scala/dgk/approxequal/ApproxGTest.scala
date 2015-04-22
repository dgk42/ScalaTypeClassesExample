package dgk.approxequal

import scala.reflect.ClassTag
import org.scalacheck.Arbitrary
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait CheckGLaws extends FunSuite with Checkers {
  def checkLaws[T, U <: ApproxEqualG[T]](subject: String)(implicit ev: Arbitrary[U]): Unit = {
    ApproxEqualGLawProperties.laws[T, U].properties foreach {
      case (name, law) =>
        test(s"$name for $subject") {
          check(law)
        }
    }
  }
}

@RunWith(classOf[JUnitRunner])
class ApproxGTest extends CheckGLaws {
  import GGenerators._

  // NOTE: We have to do property testing for the wrapper classes.
  checkLaws[Int, IntApproxEqualG]("Int")
  checkLaws[Double, DoubleApproxEqualG]("Double")
  checkLaws[Vec2D, Vec2D1ApproxEqualG]("Vec2D")
  checkLaws[List[IntApproxEqualG], ListApproxEqualG[Int, IntApproxEqualG]]("IntList")
  checkLaws[List[DoubleApproxEqualG], ListApproxEqualG[Double, DoubleApproxEqualG]]("DoubleList")
  checkLaws[List[Vec2D1ApproxEqualG], ListApproxEqualG[Vec2D, Vec2D1ApproxEqualG]]("Vec2DList")

  type P11 = (IntApproxEqualG, Vec2D1ApproxEqualG)
  type P12 = ProductApproxEqualG[Int, IntApproxEqualG, Vec2D, Vec2D1ApproxEqualG]
  checkLaws[P11, P12]("IntVec2DPair")

  // OMG! You can clearly see where this is getting at...
  type P2111 = (DoubleApproxEqualG, DoubleApproxEqualG)
  type P2112 = ProductApproxEqualG[Double, DoubleApproxEqualG, Double, DoubleApproxEqualG]
  type P211 = List[P2112]
  type P212 = ListApproxEqualG[P2111, P2112]
  type P221 = (Vec2D1ApproxEqualG, IntApproxEqualG)
  type P222 = ProductApproxEqualG[Vec2D, Vec2D1ApproxEqualG, Int, IntApproxEqualG]
  type P21 = (P212, P222)
  type P22 = ProductApproxEqualG[P211, P212, P221, P222]
  checkLaws[P21, P22]("WILDPair")

  // NOTE: We don't have to use another test class to test the alternate Vec2 distance metric.
  checkLaws[Vec2D, Vec2D2ApproxEqualG]("AnotherVec2D")
  type P31 = (Vec2D2ApproxEqualG, Vec2D2ApproxEqualG)
  type P32 = ProductApproxEqualG[Vec2D, Vec2D2ApproxEqualG, Vec2D, Vec2D2ApproxEqualG]
  checkLaws[P31, P32]("AnotherVec2DPair")
}
