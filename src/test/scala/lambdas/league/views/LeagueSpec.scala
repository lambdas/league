package lambdas.league.views

import lambdas.league.models.{Team, WLStats}
import lambdas.league.utils
import org.scalatest._

class LeagueSpec extends FlatSpec with Matchers {
  "league page" should "have a table with all the teams" in {
    val stats = utils.allTeamNames.map(Team(_) -> WLStats.zero).toMap
    val html = V.html.league(stats).toString
    utils.allTeamNames.foreach { name => html should include(name) }
  }

  it should "display team wins/loses statistics" in {
    val stats = Map(Team("Atlanta Hawks") -> WLStats(nWins = 10, nLosses = 5, nHidden = 3))
    val html = V.html.league(stats).toString
    html should include("<td>10</td>")
    html should include("<td>5</td>")
    html should include("<td>3</td>")
  }
}
