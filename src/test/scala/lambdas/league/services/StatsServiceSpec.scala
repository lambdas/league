package lambdas.league.services

import java.time.LocalDate

import lambdas.league.models.{GameResult, Team, WLStats}
import org.scalatest.{FlatSpec, Matchers}

class StatsServiceSpec extends FlatSpec with Matchers {
  "Stats service" should "aggregate game results into stats" in {
    val events = Set(
      GameResult(Team("Atlanta Hawks"), Team("Miami Heat"),     100, 110, LocalDate.of(2019, 1, 1), false),
      GameResult(Team("Atlanta Hawks"), Team("Boston Celtics"), 100, 110, LocalDate.of(2019, 1, 2), false),
      GameResult(Team("Atlanta Hawks"), Team("Denver Nuggets"), 100, 110, LocalDate.of(2019, 1, 2), true),
    )
    StatsService.stats(events) shouldBe Map(
      Team("Atlanta Hawks")  -> WLStats(0, 2, 1),
      Team("Miami Heat")     -> WLStats(1, 0, 0),
      Team("Boston Celtics") -> WLStats(1, 0, 0),
      Team("Denver Nuggets") -> WLStats(0, 0, 1),
    )
  }
}
