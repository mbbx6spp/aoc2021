package aoc2021

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object Day1Props extends Properties("Day1") {
  property("prop1") = forAll { (a: String, b: String) =>
    (a+b).startsWith(a)
  }
}