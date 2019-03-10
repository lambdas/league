package lambdas.league.views

import java.time.LocalDate

import lambdas.league.models.GameResult
import org.scalatest.{FlatSpec, Matchers}

class ResultsSpec extends FlatSpec with Matchers {
  "Results page" should "have a table with all results" in {
    val results = Seq(
      GameResult("Atlanta Hawks", "Miami Heat",     100, 110, LocalDate.of(2019, 1, 1), true),
      GameResult("Atlanta Hawks", "Boston Celtics", 105, 115, LocalDate.of(2019, 1, 2), true),
      GameResult("Atlanta Hawks", "Denver Nuggets", 120, 125, LocalDate.of(2019, 1, 2), false))
    val html = V.html.results(results).toString.replaceAll("\n", " ")

    html should include regex "<tr>.*2019-01-01.*Atlanta Hawks.*100.*110.*Miami Heat.*</tr>"
    html should include regex "<tr>.*2019-01-02.*Atlanta Hawks.*105.*115.*Boston Celtics.*</tr>"
    html should include regex "<tr>.*2019-01-02.*Atlanta Hawks.*\\?.*\\?.*Denver Nuggets.*</tr>"
  }
}
