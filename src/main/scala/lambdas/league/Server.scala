package lambdas.league

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import cats.data.Kleisli
import cats.effect._
import cats.syntax.functor._
import cats.instances.list._
import cats.syntax.traverse._
import cats.syntax.apply._
import lambdas.league.models.{GameResult, Team, WLStats}
import lambdas.league.scraper.Scraper
import lambdas.league.store.DbStore
import org.http4s.client.blaze.{BlazeClientBuilder, Http1Client}
import org.http4s.server.blaze._
import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    initStats *> BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(services.TeamsService(getTeams, getWLStats))
      .withHttpApp(services.ResultsService(getResults, setResultVisible))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

  private def initStats: IO[Unit] = {
    for {
      now <- IO(LocalDate.now)
      lastDate <- DbStore.lastDate[IO].map(_.getOrElse(LocalDate.of(2018, 10, 16)))
      updateDates = (0L to ChronoUnit.DAYS.between(lastDate, now.minusDays(1))).toList.map(lastDate.plusDays)
      _ <- IO(println(s"Updating $updateDates"))
      _ <- updateDates.traverse { d =>
        BlazeClientBuilder[IO](global).resource.use { client =>
          Scraper.scoreboard(client, d).flatMap { s => s.traverse(DbStore.save[IO]) }
        }
      }
    } yield ()
  }

  private val getTeams = Kleisli[IO, Unit, List[Team]] { _ =>
    IO.pure(List(
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
    ))
  }

  private val getResults = Kleisli[IO, Unit, Seq[GameResult]] { _ =>
    DbStore.load[IO].map(_.sortWith { case (a, b) => a.date.isAfter(b.date) })
  }

  private val getWLStats = Kleisli[IO, Team, WLStats] { team =>
    DbStore.load[IO].map(_.toSet).map(WLStats.fromResults).map(_.getOrElse(team, WLStats.zero))
  }

  private val setResultVisible = Kleisli[IO, Long, Unit] { id =>
    DbStore.setVisible[IO](id)
  }
}
