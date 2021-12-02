package aoc2021

import cats.data._
import cats.syntax._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2._

import org.scalacheck.Properties
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

import aoc2021.Day1
import aoc2021.{State => Day1State}

object Day1Props extends Properties("Day1") {
  val genWindowSize = Gen.choose[Int](1, 10).suchThat(_ > 0)
  val genSizeFactor = Gen.choose[Int](3, 100).suchThat(_ > 1)
  val genDepth      = Gen.choose[Int](100, 500000)

  property("constant sequence yields increaseCount of zero for any sliding window size") =
    forAll(genWindowSize, genSizeFactor) { (windowSize: Int, sizeFactor: Int) =>
      val length = sizeFactor*windowSize
      val constantS = Stream.constant("555").take(sizeFactor*windowSize)
      runTest(constantS, windowSize) == Some(0)
    }
  
  property("singleton stream yields zero increaseCount") =
    forAll(genWindowSize, genDepth) { (windowSize: Int, depth: Int) =>
      val singletonS = Stream.constant(depth.toString).take(1)
      runTest(singletonS, windowSize) == Some(0)
    }
  
  def runTest(stream: Stream[IO, String], windowSize: Int): Option[Int] =
    Day1.solution(stream)(windowSize).compile.last.unsafeRunSync().map(_.increaseCount)
}