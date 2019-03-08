package lambdas.league.services

import cats.instances.map._
import cats.instances.set._
import cats.syntax.foldable._
import lambdas.league.models.WLStats.fromResult
import lambdas.league.models.{GameResult, Team, WLStats}

object StatsService {
  def stats(results: Set[GameResult]): Map[Team, WLStats] = results.unorderedFoldMap(fromResult)
}
