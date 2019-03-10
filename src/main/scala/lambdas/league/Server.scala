package lambdas.league

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import cats.data.Kleisli
import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import lambdas.league.models.{Team, WLStats}
import lambdas.league.scraper.Scraper
import lambdas.league.store.DbStore
import org.http4s.client.blaze.Http1Client
import org.http4s.server.blaze._
import cats.syntax.traverse._
import cats.syntax.functor._
import cats.instances.list._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] {
  private var stats: Map[Team, WLStats] = _

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    Stream.eval(initStats).flatMap { _ =>
      BlazeBuilder[IO]
        .bindHttp(8080, "localhost")
        .mountService(services.TeamsService(getTeams, getWLStats), "/")
        .serve
    }
  }

  private def initStats: IO[Unit] = {
//    val seasonStart = LocalDate.of(2018, 10, 16)
//    Http1Client[IO]().flatMap { c =>
//      (0L to ChronoUnit.DAYS.between(seasonStart, LocalDate.now))
//        .toList
//        .map(seasonStart.plusDays)
//        .traverse { d =>
//          println(s"Fetching $d")
//          Scraper.scoreboard(c, d).flatMap { s => s.traverse(DbStore.save[IO]) }
//        }.void
//    }
    IO.unit
  }

  private val getTeams = Kleisli[IO, Unit, Set[Team]] { _ =>
    IO.pure(Set(
      "ATL",
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
    ))
  }

  private val getWLStats = Kleisli[IO, Team, WLStats] { team =>
    DbStore.load[IO].map(_.toSet).map(WLStats.fromResults).map(_.getOrElse(team, WLStats.zero))
  }
}
