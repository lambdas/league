package lambdas.league.scraper

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

import cats.effect.Sync
import cats.instances.either._
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.foldable._
import cats.syntax.functor._
import cats.syntax.monadError._
import io.circe.Json
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.Client
import org.http4s.{Header, Headers, Request, Uri}

object Scraper {
  private val headers = Headers(
    Header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"),
    Header("Accept-encoding", "deflate, br"),
    Header("Accept-language", "en-US,en;q=0.9"))

  def scoreboard[F[_]: Sync](httpClient: Client[F], date: LocalDate): F[List[GameResult]] = {
    httpClient
      .expect[Json](Request[F](uri = uri(date), headers = headers))
      .map(parse)
      .map(_.leftMap(new Throwable(_)))
      .rethrow
  }

  def parse(scoreboard: Json): Either[String, List[GameResult]] = {
    scoreboard
      .as[ResponseDao]
      .bimap(_.toString(),  r => (r.gameHeaders, r.lineScores))
      .flatMap { case (hs, ls) => results(hs, ls) }
  }

  private def results(headers: List[GameHeaderDao], scores: List[LineScoreDao]): Either[String, List[GameResult]] = {
    headers
      .map { h => (h, scores.filter(_.gameSeq == h.gameSeq)) }
      .foldM(List.empty[GameResult]) {
        case (acc, (h, a :: b :: Nil)) => (result(h, a, b) :: acc).asRight
        case _ => "wrong number of line scores, must be exactly 2".asLeft
      }
  }

  private def result(header: GameHeaderDao, score1: LineScoreDao, score2: LineScoreDao): GameResult = {
    val (road, home) = if (header.roadTeamId == score1.teamId) (score1, score2) else (score2, score1)
    GameResult(header.date, road.teamCode, home.teamCode, road.score, home.score)
  }

  private def uri(date: LocalDate): Uri = {
    Uri.uri("https://stats.nba.com/stats/scoreboardv2")
      .withQueryParam("DayOffset", 0)
      .withQueryParam("GameDate", ISO_LOCAL_DATE.format(date))
      .withQueryParam("LeagueID", "00")
  }
}
