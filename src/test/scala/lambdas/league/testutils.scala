package lambdas.league

import cats.Id
import cats.effect.{ExitCase, Sync}

import scala.annotation.tailrec

object testutils {
  val allTeamNames = Set(
    "Atlanta Hawks",
    "Boston Celtics",
    "Brooklyn Nets",
    "Charlotte Hornets",
    "Chicago Bulls",
    "Cleveland Cavaliers",
    "Dallas Mavericks",
    "Denver Nuggets",
    "Detroit Pistons",
    "Golden State Warriors",
    "Houston Rockets",
    "Indiana Pacers",
    "LA Clippers",
    "Los Angeles Lakers",
    "Miami Heat",
    "Milwaukee Bucks",
    "Minnesota Timberwolves",
    "New Orleans Pelicans",
    "New York Knicks",
    "Oklahoma City Thunder",
    "Orlando Magic",
    "Philadelphia 76ers",
    "Phoenix Suns",
    "Portland Trail Blazers",
    "Sacramento Kings",
    "San Antonio Spurs",
    "Toronto Raptors",
    "Utah Jazz",
    "Washington Wizards",
  )

  implicit val sync: Sync[Id] = new Sync[Id] {
    def suspend[A](thunk: => Id[A]): Id[A] = thunk

    def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] = {
      val a = acquire
      try { use(a) } finally { release(a, ExitCase.Completed) }
    }

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = f(a) match {
      case Right(res) => res
      case Left(res) => tailRecM(res)(f)
    }

    def raiseError[A](e: Throwable): Id[A] = throw e

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = fa

    def pure[A](x: A): Id[A] = x
  }
}
