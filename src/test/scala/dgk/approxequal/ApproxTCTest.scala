package dgk.approxequal

import scala.reflect.ClassTag
import org.scalacheck.Arbitrary
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.prop.Checkers

trait CheckTCLaws extends FunSuite with Checkers {
  def checkLaws[T](subject: String)(
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T]): Unit = {

    ApproxEqualTCLawProperties.laws[T].properties foreach {
      case (name, law) =>
        test(s"$name for $subject") {
          check(law)
        }
    }
  }

  def checkLaws[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T],
    ev4: ClassTag[T]): Unit = {

    checkLaws[T](ev4.toString)
  }
}

@RunWith(classOf[JUnitRunner])
class ApproxTCTest extends CheckTCLaws {
  import IntWithDistanceTC._
  import IntApproxEqualTC._
  import DoubleWithDistanceTC._
  import DoubleApproxEqualTC._
  import Vec2DGenerator._
  import Vec2DWithDistanceTC._
  import Vec2DApproxEqualTC._
  import ListApproxEqualTC._
  import ProductApproxEqualTC._

  checkLaws[Int]
  checkLaws[Double]
  checkLaws[Vec2D]
  checkLaws[List[Int]]("IntList")
  /*
   * NOTE: We use the overloaded checkLaws(subject: String) method to avoid the
   *   org.scalatest.exceptions.DuplicateTestNameException 
   *   during run-time, since the following List[T] types will register the same names.
   *   Reference:
   *     http://doc.scalatest.org/2.2.4/index.html#org.scalatest.exceptions.DuplicateTestNameException
   */
  checkLaws[List[Double]]("DoubleList")
  checkLaws[List[Vec2D]]("Vec2DList")
  checkLaws[(Int, Vec2D)]("IntVec2DPair")
  checkLaws[(List[(Double, Double)], (Vec2D, Int))]("WILDPair")
}
