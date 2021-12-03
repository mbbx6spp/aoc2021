package aoc2021

import cats.data._
import cats.syntax.all._
import cats.effect.{IO, IOApp, Concurrent}
import cats.effect.std.Console

import fs2._
import fs2.io._
import fs2.io.file.{Files, Path}
import scala.util.Try

case class State(previous: Option[Int], increaseCount: Int)
object State {
  def empty: State = State(None, 0)
}

object Day1 extends AdventOfCode {
  def foldSuccess(state: State)(elem: Int): State = 
    state match {
      case State(None, n) => State(Some(elem), n)
      case State(Some(prev), n) if elem > prev => State(Some(elem), n+1)
      case State(_, n) => State(Some(elem), n)
    }

  def solution[F[_]: Concurrent](input: Stream[F, String])(n: Int): Stream[F, State] = 
    input
      .through(text.lines)
      .map((s: String) => Try(s.toInt).toEither)
      .sliding(n)
      .fold(State.empty)((state, chunk) => chunk.toList.sequence.map(_.sum).fold(err => State.empty, foldSuccess(state)(_)))

  def part1 = output[IO, State, Int](solution(fromFile("./data/day1"))(1))(_.increaseCount)
  def part2 = output[IO, State, Int](solution(fromFile("./data/day1"))(3))(_.increaseCount)
  def run = runSolution(part1, part2)
}