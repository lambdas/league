package lambdas.league.views

import lambdas.league.models.Team
import lambdas.league.utils
import org.scalatest._

class LeagueSpec extends FlatSpec with Matchers {
  "league page" should "have a table with all the teams" in {
    val teams = utils.allTeamNames.map(Team(_)).toSeq
    val html = V.html.league(teams).toString
    utils.allTeamNames.foreach { name => html should include(name) }
  }
}
