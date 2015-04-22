package dgk.approxequal

import scalaz._
import Scalaz._
import std.boolean.conditional

trait WithDistanceG[T] {
  def v: T
  def distance(that: T): Double
}

trait ApproxEqualG[T] extends WithDistanceG[T] {
  // NOTE: We need access to the v member.
  def approxEqual(that: ApproxEqualG[T]): Boolean = distance(that.v) < Helpers.EPS

  def =~= = approxEqual(_)
  def =#= = { that: ApproxEqualG[T] =>
    !approxEqual(that)
  }
}

object ApproxEqualGLaws {
  def commutative[T](t1: ApproxEqualG[T], t2: ApproxEqualG[T]): Boolean =
    (t1 =~= t2) == (t2 =~= t1)
  def reflexive[T](t: ApproxEqualG[T]): Boolean = t =~= t
  def transitive[T](t1: ApproxEqualG[T], t2: ApproxEqualG[T], t3: ApproxEqualG[T]): Boolean =
    conditional(
      (t1 =~= t2) && (t2 =~= t3),
      (t1 =~= t3))
}

class IntApproxEqualG(val v: Int) extends ApproxEqualG[Int] {
  def distance(that: Int): Double = math.abs(v - that).toDouble
}

class DoubleApproxEqualG(val v: Double) extends ApproxEqualG[Double] {
  def distance(that: Double): Double = math.abs(v - that)
}

class Vec2D1ApproxEqualG(val v: Vec2D) extends ApproxEqualG[Vec2D] {
  def distance(that: Vec2D): Double = v euclideanDistance that
}

class Vec2D2ApproxEqualG(val v: Vec2D) extends ApproxEqualG[Vec2D] {
  def distance(that: Vec2D): Double = v manhattanDistance that
}

class ListApproxEqualG[T, U <: ApproxEqualG[T]](
  val v: List[U]) extends ApproxEqualG[List[U]] {

  // NOTE: This is dummy because the following approxEqual() method does not use it.
  def distance(that: List[U]): Double = (0.0 /: (v zip that)) {
    case (acc, (x, y)) => acc + (x distance y.v)
  }

  override def approxEqual(that: ApproxEqualG[List[U]]): Boolean =
    v.size == that.v.size && ((v zip that.v) forall {
      case (x, y) => x approxEqual y
    })
}
// TODO: Rinse && Repeat for Option monad.

class ProductApproxEqualG[T, U <: ApproxEqualG[T], V, W <: ApproxEqualG[V]](
  val v: (U, W)) extends ApproxEqualG[(U, W)] {

  // NOTE: This is dummy because the following approxEqual() method does not use it.
  def distance(that: (U, W)): Double =
    (v._1 distance that._1.v) + (v._2 distance that._2.v)

  override def approxEqual(that: ApproxEqualG[(U, W)]): Boolean =
    (v._1 approxEqual that.v._1) && (v._2 approxEqual that.v._2)
}
// TODO: Rinse && Repeat for Either monad.

object GExample extends App {
  def printDistance[T](t1: WithDistanceG[T], t2: WithDistanceG[T]): Unit = {
    val dist = t1 distance t2.v
    println(dist)
  }

  final val TT = Symbol("#t")
  final val FF = Symbol("#f")

  def showAndPrintOut[T](v1: ApproxEqualG[T], v2: ApproxEqualG[T]): Symbol = {
    val s = if (v1 =~= v2) TT else FF
    println(f"$s%s (${v1 distance v2.v}%.4f)")
    s
  }

  // NOTE: We have to use the wrapper classes (e.g. IntApproxEqualG).

  // 42 ~= 42 is TRUE [|42 - 42| == 0 < 0.001]
  val t11 = new IntApproxEqualG(42)
  val t12 = new IntApproxEqualG(42)
  val t1 = showAndPrintOut(t11, t12)

  // 42 ~= 69105 is FALSE [|42 - 69105| == 69063 > 0.001]
  val t21 = new IntApproxEqualG(42)
  val t22 = new IntApproxEqualG(69105)
  val t2 = showAndPrintOut(t21, t22)

  // 5.01 ~= 5.02 is FALSE [|5.01 - 5.02| == 0.01 > 0.001]
  val t31 = new DoubleApproxEqualG(5.01)
  val t32 = new DoubleApproxEqualG(5.02)
  val t3 = showAndPrintOut(t31, t32)

  // 5.00005 ~= 5.0008 is TRUE [|5.00005 - 5.0008| == 0.00075 < 0.001]
  val t41 = new DoubleApproxEqualG(5.00005)
  val t42 = new DoubleApproxEqualG(5.0008)
  val t4 = showAndPrintOut(t41, t42)

  // 5.1 + 2.8j ~= 5.1001 + 2.805j is FALSE
  //   [sqrt((5.1 - 5.1001)^2 + (2.8 - 2.805)^2) ~= 0.005 > 0.001]
  val t51 = new Vec2D1ApproxEqualG(Vec2D(5.1, 2.8))
  val t52 = new Vec2D1ApproxEqualG(Vec2D(5.1001, 2.805))
  val t5 = showAndPrintOut(t51, t52)
  val t61 = new Vec2D2ApproxEqualG(Vec2D(5.1, 2.8))
  val t62 = new Vec2D2ApproxEqualG(Vec2D(5.1001, 2.805))
  val t6 = showAndPrintOut(t61, t62)

  // Counter-example to verify that the property tests need tweaking:
  //   let a = 1.0001, b = 1.001, c = 1.0012  
  //   a ~= b is TRUE
  //   b ~= c is TRUE
  //   a ~= c is FALSE => Transitivity law does not hold,
  //     i.e. (a ~= b) ^ (b ~= c) does NOT imply (a ~= c)! 
  val t711 = new DoubleApproxEqualG(1.0001)
  val t712 = new DoubleApproxEqualG(1.001)
  val t713 = new DoubleApproxEqualG(1.0012)
  val t71 = showAndPrintOut(t711, t712)
  val t72 = showAndPrintOut(t712, t713)
  val t73 = showAndPrintOut(t711, t713)

  assert(List(t1, t2, t3, t4, t5, t6, t71, t72, t73) == List(TT, FF, FF, TT, FF, FF, TT, TT, FF))
}
