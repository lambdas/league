package lambdas.league.views

import lambdas.league.models.WLStats
import lambdas.league.testutils.{allTeams, team}
import org.scalatest._

class StandingsViewSpec extends FlatSpec with Matchers {
  "League page" should "have a table with all the teams" in {
    val stats = allTeams.map(_ -> WLStats.zero)
    val html = V.html.standings(stats).toString
    allTeams.foreach { team => html should include(team.name) }
  }

  it should "display team wins/loses statistics" in {
    val stats = List(team("ATL") -> WLStats(nWins = 10, nLosses = 5, nHidden = 3))
    val html = V.html.standings(stats).toString
    html should include("<td>10</td>")
    html should include("<td>5</td>")
    html should include("<td>3</td>")
  }

  it should "group teams by conference" in {
    val stats = List(
      team("ATL") -> WLStats(nWins = 10, nLosses = 5, nHidden = 3),
      team("DEN") -> WLStats(nWins = 10, nLosses = 5, nHidden = 3),
    )
    val html = V.html.standings(stats).toString.replaceAll("\n", " ")
    html should include regex "East.*Atlanta Hawks.*West.*Denver Nuggets.*</tr>"
  }
}
