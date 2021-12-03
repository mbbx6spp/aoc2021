package aoc2021

import cats._
import cats.syntax.all._
import cats.effect.{IO, IOApp, Concurrent}
import cats.effect.std.Console

import fs2._
import fs2.io._
import fs2.io.file.{Files, Path}
import scala.util.Try

case class Position(x: Integer, y: Integer, aim: Integer)
object Position {
  def empty = Position(0, 0, 0)
  // interpreter for part1
  def next(pos: Position)(cmd: Command): Position = cmd match {
    case Command(Direction.Up, y)      => pos.copy(y = pos.y - y)
    case Command(Direction.Down, y)    => pos.copy(y = pos.y + y)
    case Command(Direction.Forward, x) => pos.copy(x = pos.x + x)
    case Command(Direction.Nowhere, _) => pos
  }
  // interpreter for part2
  def next2(pos: Position)(cmd: Command): Position = cmd match {
    case Command(Direction.Up, aim)      => pos.copy(aim = pos.aim - aim)
    case Command(Direction.Down, aim)    => pos.copy(aim = pos.aim + aim)
    case Command(Direction.Forward, x)   => pos.copy(x = pos.x + x, y = pos.y + pos.aim * x)
    case Command(Direction.Nowhere, _)   => pos
  }
}

case class Command(direction: Direction, value: Int) { override def toString = s"${direction.toString} ${value.toString}" }
object Command {
  def fromString(cmd: String) = cmd.split(" ") match {
    case Array(rawDirection, rawValue) => (Direction(rawDirection), Try(rawValue.toInt).toOption).mapN(Command(_, _))
    case _                             => None
  }

  def empty: Command = Command(Direction.Nowhere, 0)
}

sealed trait Direction
object Direction {
  case object Up extends Direction      { override def toString = "up" }
  case object Down extends Direction    { override def toString = "down" }
  case object Forward extends Direction { override def toString = "forward" }
  case object Nowhere extends Direction { override def toString = "nowhere" }

  def apply(s: String): Option[Direction] = s match {
    case "up"      => Up.some
    case "down"    => Down.some
    case "forward" => Forward.some
    case "nowhere" => Nowhere.some
  }

  def choices: Seq[Direction] = Seq(Up, Down, Forward)
}

object Day2 extends AdventOfCode {
  def solution(folder: (Position, Command) => Position) =
    fromFile("./data/day2")
      .through(text.lines)
      .map(Command.fromString(_).getOrElse(Command.empty))
      .fold(Position.empty)(folder)

  def part1 = output(solution(Position.next(_)(_)))(pos => pos.x * pos.y)
  def part2 = output(solution(Position.next2(_)(_)))(pos => pos.x * pos.y)

  def run = runSolution(part1, part2)
}