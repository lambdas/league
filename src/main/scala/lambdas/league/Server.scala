package lambdas.league

import java.sql.{Connection, DriverManager}
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Properties

import cats.data.Kleisli
import cats.effect._
import cats.instances.list._
import cats.syntax.apply._
import cats.syntax.functor._
import cats.syntax.traverse._
import lambdas.league.models.{GameResult, GameType, Team, TeamCode, TeamName, WLStats}
import lambdas.league.scraper.Scraper
import lambdas.league.store.DbStore
import lambdas.league.utils.http._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    initStats *> BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(
        services.StandingsService(getTeams, getWLStats)
          .and(services.ResultsService(getResults, setResultVisible))
          .orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

  private def db[F[_]: Sync]: Resource[F, Connection] = {
    Resource.make(openConnection[F])(conn => Sync[F].delay(conn.close()))
  }

  private def openConnection[F[_]: Sync]: F[Connection] = Sync[F].delay {
    val props = new Properties
    props.setProperty("user", "postgres")
    props.setProperty("password", "whatevs")
    props.setProperty("ssl", "false")
    DriverManager.getConnection("jdbc:postgresql://localhost:5432/league", props)
  }

  private def initStats: IO[Unit] = {
    for {
      lastDate <- DbStore.lastDate[IO](db).map(_.getOrElse(LocalDate.of(2018, 10, 16)))
      seasonEnd = LocalDate.of(2019, 4, 10)
      updateDates = (0L to ChronoUnit.DAYS.between(lastDate, seasonEnd)).toList.map(lastDate.plusDays)
      _ <- IO(println(s"Updating $updateDates"))
      _ <- updateDates.traverse { d =>
        BlazeClientBuilder[IO](global).resource.use { client =>
          Scraper.scoreboard(client, d).flatMap { s => 
            s.traverse(r => DbStore.saveResult[IO](db, store.GameResult.fromScraper(r, 2018, GameType.Regular))) 
          }
        }
      }
    } yield ()
  }

  private val getTeams = Kleisli[IO, Unit, List[Team]] { _ =>
    DbStore.teams[IO](db)
  }

  private val getResults = Kleisli[IO, Unit, Seq[GameResult]] { _ =>
    DbStore.results[IO](db, 1, 2018).map(_.sortWith { case (a, b) => a.date.isAfter(b.date) })
  }

  private val getWLStats = Kleisli[IO, Unit, List[WLStats]] { team =>
    DbStore.wlStats[IO](db, 1, 2018)
  }

  private val setResultVisible = Kleisli[IO, Long, Unit] { resultId =>
    DbStore.setVisible[IO](db, 1, resultId)
  }
}

