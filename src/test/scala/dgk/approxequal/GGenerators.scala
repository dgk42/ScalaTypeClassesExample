package dgk.approxequal

import org.scalacheck.Gen
import org.scalacheck.Arbitrary
import Vec2DGenerator._

// NOTE: We need to write arbitrary generators for each wrapper class.
object GGenerators {
  implicit lazy val arbGInt: Arbitrary[IntApproxEqualG] = Arbitrary {
    for {
      v <- Arbitrary.arbitrary[Int]
    } yield new IntApproxEqualG(v)
  }

  implicit lazy val arbGDouble: Arbitrary[DoubleApproxEqualG] = Arbitrary {
    for {
      v <- Arbitrary.arbitrary[Double]
    } yield new DoubleApproxEqualG(v)
  }

  implicit lazy val arbGVec2D1: Arbitrary[Vec2D1ApproxEqualG] = Arbitrary {
    for {
      v <- Arbitrary.arbitrary[Vec2D]
    } yield new Vec2D1ApproxEqualG(v)
  }

  implicit lazy val arbGVec2D2: Arbitrary[Vec2D2ApproxEqualG] = Arbitrary {
    for {
      v <- Arbitrary.arbitrary[Vec2D]
    } yield new Vec2D2ApproxEqualG(v)
  }

  // NOTE: We need to write arbitrary generators even for the container types.

  implicit def arbGList[T, U <: ApproxEqualG[T]](
    implicit ev: Arbitrary[U]): Arbitrary[ListApproxEqualG[T, U]] = {

    Arbitrary {
      for {
        v <- Gen.containerOf[List, U](ev.arbitrary)
      } yield new ListApproxEqualG(v)
    }
  }
  // TODO: Rinse && Repeat for Option monad.

  implicit def arbGTuple[T, U <: ApproxEqualG[T], V, W <: ApproxEqualG[V]](
    implicit ev1: Arbitrary[U], ev2: Arbitrary[W]): Arbitrary[ProductApproxEqualG[T, U, V, W]] = {

    Arbitrary {
      for {
        fst <- ev1.arbitrary
        snd <- ev2.arbitrary
      } yield new ProductApproxEqualG((fst, snd))
    }
  }
  // TODO: Rinse && Repeat for Either monad.
}
