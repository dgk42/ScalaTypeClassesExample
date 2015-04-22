package dgk.approxequal

import scala.language.implicitConversions
import scalaz._
import Scalaz._
import std.boolean.conditional

trait WithDistanceTC[T] {
  def distance(t1: T, t2: T): Double
}

trait ApproxEqualTC[T] {
  def approxEqual(t1: T, t2: T)(implicit ev: WithDistanceTC[T]): Boolean =
    (ev distance (t1, t2)) < Helpers.EPS
}

// NOTE: Compare the following syntax definition with the ApproxEqualG counterpart.
trait ApproxEqualTCSyntax[T] {
  def =~=(t: T): Boolean
  def =#=(t: T): Boolean
}

object ApproxEqualTCSyntax {
  implicit def approxEqualTCSyntax[T](t1: T)(
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T]): ApproxEqualTCSyntax[T] = {

    new ApproxEqualTCSyntax[T] {
      def =~=(t2: T): Boolean = ev2 approxEqual (t1, t2)
      def =#=(t2: T): Boolean = !(t1 =~= t2)
    }
  }
}

// NOTE: We need implicit declarations.
trait ApproxEqualTCLaws[T] {
  implicit val ev1: WithDistanceTC[T]
  implicit val ev2: ApproxEqualTC[T]

  def commutative(t1: T, t2: T): Boolean =
    (ev2 approxEqual (t1, t2)) == (ev2 approxEqual (t2, t1))
  def reflexive(t: T): Boolean = ev2 approxEqual (t, t)
  def transitive(t1: T, t2: T, t3: T): Boolean =
    conditional(
      (ev2 approxEqual (t1, t2)) && (ev2 approxEqual (t2, t3)),
      (ev2 approxEqual (t1, t3)))
}

object IntWithDistanceTC {
  implicit val intHasDistance = new WithDistanceTC[Int] {
    def distance(t1: Int, t2: Int): Double = math.abs(t1 - t2).toDouble
  }
}

object IntApproxEqualTC {
  implicit val intIsApproxEqual = new ApproxEqualTC[Int] {}
}

object DoubleWithDistanceTC {
  implicit val doubleHasDistance = new WithDistanceTC[Double] {
    def distance(t1: Double, t2: Double): Double = math.abs(t1 - t2)
  }
}

object DoubleApproxEqualTC {
  implicit val doubleIsApproxEqual = new ApproxEqualTC[Double] {}
}

object Vec2DWithDistanceTC {
  implicit val vec2DHasDistance = new WithDistanceTC[Vec2D] {
    def distance(t1: Vec2D, t2: Vec2D): Double = t1 euclideanDistance t2
  }
}

object AnotherVec2DWithDistanceTC {
  implicit val anotherVec2DHasDistance = new WithDistanceTC[Vec2D] {
    def distance(t1: Vec2D, t2: Vec2D): Double = t1 manhattanDistance t2
  }
}

object Vec2DApproxEqualTC {
  implicit val vec2DIsApproxEqual = new ApproxEqualTC[Vec2D] {}
}

object ListApproxEqualTC {
  // NOTE: This is dummy because the following listApproxEqual() function does not use it.
  implicit def dummyListDistance[T](implicit ev: WithDistanceTC[T]): WithDistanceTC[List[T]] =
    new WithDistanceTC[List[T]] {
      def distance(t1: List[T], t2: List[T]): Double = (0.0 /: (t1 zip t2)) {
        case (acc, (x, y)) => acc + (ev distance (x, y))
      }
    }

  implicit def listApproxEqual[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T]): ApproxEqualTC[List[T]] = {

    new ApproxEqualTC[List[T]] {
      override def approxEqual(t1: List[T], t2: List[T])(
        implicit ev: WithDistanceTC[List[T]]): Boolean = {

        t1.size == t2.size && ((t1 zip t2) forall {
          case (x, y) => ev2 approxEqual (x, y)
        })
      }
    }
  }
}
// TODO: Rinse && Repeat for Option monad.

object ProductApproxEqualTC {
  // NOTE: This is dummy because the following productApproxEqual() function does not use it.
  implicit def dummyProdDistance[T, U](
    implicit ev1: WithDistanceTC[T], ev2: WithDistanceTC[U]): WithDistanceTC[(T, U)] = {

    new WithDistanceTC[(T, U)] {
      def distance(t1: (T, U), t2: (T, U)): Double =
        (ev1 distance (t1._1, t2._1)) + (ev2 distance (t1._2, t2._2))
    }
  }

  implicit def productApproxEqual[T, U](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T],
    ev3: WithDistanceTC[U], ev4: ApproxEqualTC[U]): ApproxEqualTC[(T, U)] = {

    new ApproxEqualTC[(T, U)] {
      override def approxEqual(a: (T, U), b: (T, U))(
        implicit ev: WithDistanceTC[(T, U)]): Boolean = {

        (ev2 approxEqual (a._1, b._1)) && (ev4 approxEqual (a._2, b._2))
      }
    }
  }
}
// TODO: Rinse && Repeat for Either monad.

object TCExample extends App {
  import IntWithDistanceTC._
  import IntApproxEqualTC._
  import DoubleWithDistanceTC._
  import DoubleApproxEqualTC._
  import Vec2DWithDistanceTC._
  import AnotherVec2DWithDistanceTC._
  import Vec2DApproxEqualTC._
  import ApproxEqualTCSyntax._

  // NOTE: Context bound example.
  def printDistance[T: WithDistanceTC](t1: T, t2: T): Unit = {
    val dist = implicitly[WithDistanceTC[T]].distance(t1, t2)
    println(dist)
  }

  final val TT = Symbol("#t")
  final val FF = Symbol("#f")

  def showAndPrintOut[T: ApproxEqualTC](v1: T, v2: T)(implicit ev: WithDistanceTC[T]): Symbol = {
    val s = if (v1 =~= v2) TT else FF
    println(f"$s%s (${ev distance (v1, v2)}%.4f)")
    s
  }

  // NOTE: We don't have to use any wrapper class. We operate on the types themselves (e.g. Int).

  // 42 ~= 42 is TRUE [|42 - 42| == 0 < 0.001]
  val t11 = 42
  val t12 = 42
  val t1 = showAndPrintOut(t11, t12)

  // 42 ~= 69105 is FALSE [|42 - 69105| == 69063 > 0.001]
  val t21 = 42
  val t22 = 69105
  val t2 = showAndPrintOut(t21, t22)

  // 5.01 ~= 5.02 is FALSE [|5.01 - 5.02| == 0.01 > 0.001]
  val t31 = 5.01
  val t32 = 5.02
  val t3 = showAndPrintOut(t31, t32)

  // 5.00005 ~= 5.0008 is TRUE [|5.00005 - 5.0008| == 0.00075 < 0.001]
  val t41 = 5.00005
  val t42 = 5.0008
  val t4 = showAndPrintOut(t41, t42)

  // 5.1 + 2.8j ~= 5.1001 + 2.805j is FALSE
  //   [sqrt((5.1 - 5.1001)^2 + (2.8 - 2.805)^2) ~= 0.005 > 0.001]
  val t51 = Vec2D(5.1, 2.8)
  val t52 = Vec2D(5.1001, 2.805)
  /*
   * NOTE: Due to importing both Vec2DWithDistanceTC and AnotherVec2DWithDistanceTC,
   *   the compiler complains about the implicit value passed to showAndPrintOut().
   *   The compiler cannot resolve which value(s) to pass implicitly to showAndPrintOut(),
   *   since both Vec2DWithDistanceTC.vec2DHasDistance and
   *   AnotherVec2DWithDistanceTC.anotherVec2DHasDistance are in scope.
   *   That's why we explicitly pass the implicit parameters.
   *
   *   In addition, check that we pass 2 implicit parameters instead of 1. That is,
   *   Function signature:
   *     showAndPrintOut[T: ApproxEqualTC](v1: T, v2: T)(implicit ev: WithDistanceTC[T]): Symbol
   *   At call site:
   *     showAndPrintOut(t51, t52)(vec2DIsApproxEqual, vec2DHasDistance)
   *   Explanation:
   *     The first implicit is attributed to the context bound ([T: ApproxEqualTC])
   *     and the second to the implicit (ev: WithDistanceTC[T]) respectively.
   */
  val t5 = showAndPrintOut(t51, t52)(vec2DIsApproxEqual, vec2DHasDistance)
  /*
   * NOTE: In contrast to GExample, we don't have to instantiate another object
   *   in order to use a different distance metric for our Vec2D objects.
   */
  val t6 = showAndPrintOut(t51, t52)(vec2DIsApproxEqual, anotherVec2DHasDistance)

  // Counter-example to verify that the property tests need tweaking:
  //   let a = 1.0001, b = 1.001, c = 1.0012  
  //   a ~= b is TRUE
  //   b ~= c is TRUE
  //   a ~= c is FALSE => Transitivity law does not hold,
  //     i.e. (a ~= b) ^ (b ~= c) does NOT imply (a ~= c)! 
  val t711 = 1.0001
  val t712 = 1.001
  val t713 = 1.0012
  val t71 = showAndPrintOut(t711, t712)
  val t72 = showAndPrintOut(t712, t713)
  val t73 = showAndPrintOut(t711, t713)

  assert(List(t1, t2, t3, t4, t5, t6, t71, t72, t73) == List(TT, FF, FF, TT, FF, FF, TT, TT, FF))
}
