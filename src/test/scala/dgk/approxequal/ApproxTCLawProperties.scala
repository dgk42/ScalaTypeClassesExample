package dgk.approxequal

import org.scalacheck.{ Arbitrary, Properties }
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

object ApproxEqualTCLawProperties {
  def approxEqualLaw[T](implicit e1: WithDistanceTC[T], e2: ApproxEqualTC[T]) =
    new ApproxEqualTCLaws[T] {
      val ev1 = e1
      val ev2 = e2
    }

  def commutativity[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T]): Prop = {

    forAll(approxEqualLaw.commutative _)
  }

  def reflexivity[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T]): Prop = {

    forAll(approxEqualLaw.reflexive _)
  }

  def transitivity[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T]): Prop = {

    forAll(approxEqualLaw.transitive _)
  }

  def laws[T](
    implicit ev1: WithDistanceTC[T], ev2: ApproxEqualTC[T], ev3: Arbitrary[T]): Properties = {

    new Properties("approxEqualTC") {
      property("commutativity") = commutativity[T]
      property("reflexivity") = reflexivity[T]
      property("transitivity") = transitivity[T]
    }
  }
}
