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

object Day1 extends IOApp.Simple {
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

  def fromFile(filename: String) =
    Files[IO].readAll(Path(filename)).through(text.utf8.decode)
  
  def output[F[_]: Console: Concurrent](input: Stream[F, State]): Stream[F, Unit] =
    input.evalMap((state: State) => Console[F].println(state.increaseCount))

  def printStream(s: String) = Stream.eval(IO.print(s))
  def part1 = output(solution(fromFile("./data/day1"))(1))
  def part2 = output(solution(fromFile("./data/day1"))(3))
  def run   = (printStream("part1 solution: ") |+| part1 |+| printStream("part2 solution: ") |+| part2).compile.drain
}