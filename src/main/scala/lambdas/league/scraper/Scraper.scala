package lambdas.league.scraper

import cats.instances.either._
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.foldable._
import io.circe.Json
import lambdas.league.models.{GameResult, Team}

object Scraper {
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
    GameResult(
      Team(s"${road.city} ${road.name}"),
      Team(s"${home.city} ${home.name}"),
      road.score,
      home.score,
      header.date,
      true)
  }
}
