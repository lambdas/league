package lambdas.league.services

import cats.Id
import cats.data.Kleisli
import io.circe._
import io.circe.literal._
import lambdas.league.models.{Team, TeamName, WLStats}
import lambdas.league.testutils._
import org.http4s.MediaType.application.json
import org.http4s.MediaType.text.html
import org.http4s.circe._
import org.http4s.headers.Accept
import org.http4s.{Headers, Request}
import org.scalatest.{FlatSpec, Matchers}

class StandingsServiceSpec extends FlatSpec with Matchers {
  "Service" should "response with JSON" in {
    val sut = StandingsService(getTeams, getWLStats)
    val req = Request[Id](headers = Headers(Accept(json)))
    val content = sut.run(req).value.get.as[Json]

    content shouldBe json"""
      {
        "ATL": {
          "nWins": 1,
          "nLosses": 2,
          "nHidden": 3
        },
        "MIA": {
          "nWins": 4,
          "nLosses": 5,
          "nHidden": 6
        }
      }"""
  }

  it should "response with HTML" in {
    val sut = StandingsService(getTeams, getWLStats)
    val req = Request[Id](headers = Headers(Accept(html)))
    val content = sut.run(req).value.get.as[String]

    content should include("<td>Atlanta Hawks</td>")
    content should include("<td>1</td>")
    content should include("<td>2</td>")
    content should include("<td>3</td>")
  }

  private val getTeams = Kleisli[Id, Unit, List[Team]](_ => List(team("ATL"), team("MIA")))
  private val getWLStats = Kleisli[Id, TeamName, WLStats] {
    case "ATL" => WLStats(1, 2, 3)
    case "MIA" => WLStats(4, 5, 6)
    case _ => WLStats.zero
  }
}
