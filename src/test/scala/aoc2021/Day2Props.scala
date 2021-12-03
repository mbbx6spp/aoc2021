package aoc2021

import cats._
import cats.syntax.all._
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2._

import org.scalacheck.Properties
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

import aoc2021.Day2

object Day2Props extends Properties("Day2") {
  val genDirection = Gen.oneOf(Direction.choices)
  val genForwardCommand = for {
    direction <- Gen.oneOf(Seq(Direction.Forward))
    value     <- Gen.choose(1, 100)
  } yield Command(direction, value)

  val genDepthCommand = for {
    direction <- Gen.oneOf(Seq(Direction.Up, Direction.Down))
    value     <- Gen.choose(1, 100)
  } yield Command(direction, value)

  val genCommand   = for {
    direction <- genDirection
    value     <- Gen.choose(1, 100)
  } yield Command(direction, value)

  val genCommandString = genCommand.map(_.toString)

  val fixtureDecoded =
    List(
      Command(Direction.Forward, 5),
      Command(Direction.Down, 5),
      Command(Direction.Forward, 8),
      Command(Direction.Up, 3),
      Command(Direction.Down, 8),
      Command(Direction.Forward, 2),
    )

  property("fixture decoding works as expected") =
    forAll { (unit: Unit) =>
      val fixture =
        """forward 5
          |down 5
          |forward 8
          |up 3
          |down 8
          |forward 2""".stripMargin
      val actual = fixture.split("\n").toList.map(Command.fromString(_))
      actual.sequence == Some(fixtureDecoded)
    }

  property("fixture summarizes to expected position for part1") =
    forAll { (unit: Unit) =>
      val expected = Position(15, 10, 0)

      val actual = fixtureDecoded.foldLeft(Position.empty)(Position.next(_)(_))
      actual == expected
    }

  property("fixture summarizes to expected position for part2") =
    forAll { (unit: Unit) =>
      val expected = Position(15, 60, 10)
      val actual = fixtureDecoded.foldLeft(Position.empty)(Position.next2(_)(_))
      actual == expected
    }

  property("arbitrary command is same as that produced by Command.fromString of its string representation") =
    forAll(genCommand) { (cmd: Command) =>
      val expected = cmd
      val actual = Command.fromString(cmd.toString)
      Some(expected) == actual
    }
  
  property("resulting position of only Ups and Downs has a zero horizontal component for part1 interpreter") =
    forAll(Gen.listOf(genDepthCommand)) { (cmds: List[Command]) =>
      cmds.foldLeft(Position.empty)(Position.next(_)(_)).x == 0
    }

  property("resulting position of any sequence of commands has a zero aim component for part1 interpreter") = 
    forAll(Gen.listOf(genCommand)) { (cmds: List[Command]) =>
      cmds.foldLeft(Position.empty)(Position.next(_)(_)).aim == 0
    }

  property("resulting position of only Forwards has a zero depth component") = 
    forAll(Gen.listOf(genForwardCommand)) { (cmds: List[Command]) =>
      cmds.foldLeft(Position.empty)(Position.next(_)(_)).y == 0
    }

  property("resulting position of only Ups and Downs has a zero horizontal component for part2 interpreter") =
    forAll(Gen.listOf(genDepthCommand)) { (cmds: List[Command]) =>
      cmds.foldLeft(Position.empty)(Position.next2(_)(_)).x == 0
    }
}