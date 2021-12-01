package aoc2021

import cats._
import cats.syntax._
import cats.implicits._
import cats.effect.{IO, IOApp}
import fs2._
import fs2.io.file.{Files, Path}
import scala.util.Try

case class State(previous: Option[Int], increaseCount: Int) {
  def isEmpty: Boolean = previous.fold(increaseCount == 0)(_ => false)
}
object State {
  def empty: State = State(None, 0)
}

object Day1 extends IOApp.Simple {

  def foldSuccess(elem: Int)(state: State): State = 
    state match {
      case State(None, n) => State(Some(elem), n)
      case State(Some(prev), n) if elem > prev => State(Some(elem), n+1)
      case State(_, n) => State(Some(elem), n)
    }
  
  def foldFailure(err: Throwable): State = State.empty

  def solution(n: Int) = 
    Files[IO]
      .readAll(Path("./data/day1part1"))
      .through(text.utf8.decode)
      .through(text.lines)
      .map((s: String) => Try(s.toInt).toEither)
      .sliding(n)
      .fold(State(None, 0))((state, chunk) => chunk.toList.sequence.map(_.sum).fold(foldFailure, foldSuccess(_)(state)))
      .evalMap((line: State) => IO.println(line))
      .compile
      .drain

  def part1 = solution(1)
  def part2 = solution(3)
  def run = part2
}