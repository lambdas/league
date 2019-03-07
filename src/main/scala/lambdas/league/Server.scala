package lambdas.league

import cats.data.Kleisli
import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import lambdas.league.models.{Team, WLStats}
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(services.TeamsService.apply(getTeams, getWLStats), "/")
      .serve
  }

  private val getTeams = Kleisli[IO, Unit, Set[Team]] { _ =>
    IO.pure(Set(
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
    ).map(Team(_)))
  }

  private val getWLStats = Kleisli[IO, Team, WLStats] { team =>
    IO.pure(WLStats.zero)
  }
}
