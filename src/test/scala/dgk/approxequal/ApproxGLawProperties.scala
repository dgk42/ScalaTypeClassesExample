package dgk.approxequal

import org.scalacheck.{ Arbitrary, Properties }
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll
import ApproxEqualGLaws._

object ApproxEqualGLawProperties {
  def commutativity[T, U <: ApproxEqualG[T]](implicit ev: Arbitrary[U]): Prop =
    forAll { (t1: U, t2: U) =>
      commutative(t1, t2)
    }

  def reflexivity[T, U <: ApproxEqualG[T]](implicit ev: Arbitrary[U]): Prop =
    forAll { t: U =>
      reflexive(t)
    }

  def transitivity[T, U <: ApproxEqualG[T]](implicit ev: Arbitrary[U]): Prop =
    forAll { (t1: U, t2: U, t3: U) =>
      transitive(t1, t2, t3)
    }

  def laws[T, U <: ApproxEqualG[T]](implicit ev: Arbitrary[U]): Properties = {
    new Properties("approxEqualTC") {
      property("commutativity") = commutativity[T, U]
      property("reflexivity") = reflexivity[T, U]
      property("transitivity") = transitivity[T, U]
    }
  }
}
