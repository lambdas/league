package lambdas.league

import cats.Id
import cats.effect.{ExitCase, Sync}
import lambdas.league.models.{Team, TeamCode}

import scala.annotation.tailrec

object testutils {
  val allTeams = List(
    Team("ATL", "Atlanta Hawks",          "east", "southeast"),
    Team("BOS", "Boston Celtics",         "east", "east"     ),
    Team("BKN", "Brooklyn Nets",          "east", "east"     ),
    Team("CHA", "Charlotte Hornets",      "east", "southeast"),
    Team("CHI", "Chicago Bulls",          "east", "center"   ),
    Team("CLE", "Cleveland Cavaliers",    "east", "center"   ),
    Team("DAL", "Dallas Mavericks",       "west", "southwest"),
    Team("DEN", "Denver Nuggets",         "west", "northwest"),
    Team("DET", "Detroit Pistons",        "east", "center"   ),
    Team("GSW", "Golden State Warriors",  "west", "pacific"  ),
    Team("HOU", "Houston Rockets",        "west", "southwest"),
    Team("IND", "Indiana Pacers",         "east", "center"   ),
    Team("LAC", "LA Clippers",            "west", "pacific"  ),
    Team("LAL", "Los Angeles Lakers",     "west", "pacific"  ),
    Team("MIA", "Miami Heat",             "east", "southeast"),
    Team("MIL", "Milwaukee Bucks",        "east", "center"   ),
    Team("MIN", "Minnesota Timberwolves", "west", "northwest"),
    Team("NOP", "New Orleans Pelicans",   "west", "southwest"),
    Team("NYK", "New York Knicks",        "east", "atlantic" ),
    Team("OKC", "Oklahoma City Thunder",  "west", "northwest"),
    Team("ORL", "Orlando Magic",          "east", "southeast"),
    Team("PHI", "Philadelphia 76ers",     "east", "atlantic" ),
    Team("PHX", "Phoenix Suns",           "west", "pacific"  ),
    Team("POR", "Portland Trail Blazers", "west", "northwest"),
    Team("SAC", "Sacramento Kings",       "west", "pacific"  ),
    Team("SAS", "San Antonio Spurs",      "west", "southwest"),
    Team("TOR", "Toronto Raptors",        "east", "atlantic" ),
    Team("UTA", "Utah Jazz",              "west", "northwest"),
    Team("WAS", "Washington Wizards",     "east", "southeast"),
  )

  val allTeamNames = allTeams.map(_.name)

  def team(code: TeamCode): Team = allTeams.find(_.code == code).get

  implicit val sync: Sync[Id] = new Sync[Id] {
    def suspend[A](thunk: => Id[A]): Id[A] = thunk

    def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

    @tailrec
    def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = f(a) match {
      case Right(res) => res
      case Left(res) => tailRecM(res)(f)
    }

    def raiseError[A](e: Throwable): Id[A] = throw e

    def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
      try { fa } catch { case e: Throwable => f(e) }

    def pure[A](x: A): Id[A] = x

    def bracketCase[A, B](acquire: Id[A])(use: A => Id[B])(release: (A, ExitCase[Throwable]) => Id[Unit]): Id[B] = {
      val a = acquire
      val res = use(a)
      release(a, ExitCase.Completed)
      res
    }
  }
}
