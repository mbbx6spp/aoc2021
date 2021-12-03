package aoc2021

import cats.data._
import cats.syntax.all._
import cats.effect.{IO, IOApp, Concurrent}
import cats.effect.std.Console

import fs2._
import fs2.io._
import fs2.io.file.{Files, Path}
import scala.util.Try

abstract class AdventOfCode extends IOApp.Simple {
  def fromFile(filename: String)  = Files[IO].readAll(Path(filename)).through(text.utf8.decode)
  def printStream(s: String)      = Stream.eval(IO.print(s))

  def output[F[_]: Console: Concurrent, S, A]
    (input: Stream[F, S])
    (transform: S => A): Stream[F, Unit] =
      input.evalMap((state: S) => Console[F].println(transform(state)))

  def runSolution[A, B](part1: Stream[IO, A], part2: Stream[IO, B])   = (
    printStream("part1 solution: ")
    |+| output(part1)(identity)
    |+| printStream("part2 solution: ")
    |+| output(part2)(identity)
  ).compile.drain
}