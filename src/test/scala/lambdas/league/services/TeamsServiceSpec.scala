package lambdas.league.services

import cats.Id
import cats.data.Kleisli
import io.circe._
import io.circe.literal._
import lambdas.league.models.{Team, WLStats}
import lambdas.league.testutils._
import org.http4s.MediaType.{`application/json`, `text/html`}
import org.http4s.circe._
import org.http4s.headers.Accept
import org.http4s.{Headers, Request}
import org.scalatest.{FlatSpec, Matchers}

class TeamsServiceSpec extends FlatSpec with Matchers {
  "Service" should "response with JSON" in {
    val sut = TeamsService(getTeams, getWLStats)
    val req = Request[Id](headers = Headers(Accept(`application/json`)))
    val content = sut.run(req).value.get.as[Json]

    content shouldBe json"""
      {
        "Atlanta Hawks": {
          "nWins": 1,
          "nLosses": 2,
          "nHidden": 3
        },
        "Miami Heat": {
          "nWins": 4,
          "nLosses": 5,
          "nHidden": 6
        }
      }"""
  }

  it should "response with HTML" in {
    val sut = TeamsService(getTeams, getWLStats)
    val req = Request[Id](headers = Headers(Accept(`text/html`)))
    val content = sut.run(req).value.get.as[String]

    content should include("<td>Atlanta Hawks</td>")
    content should include("<td>1</td>")
    content should include("<td>2</td>")
    content should include("<td>3</td>")
  }

  private val getTeams = Kleisli[Id, Unit, Set[Team]](_ => Set(Team("Atlanta Hawks"), Team("Miami Heat")))
  private val getWLStats = Kleisli[Id, Team, WLStats] {
    case Team("Atlanta Hawks") => WLStats(1, 2, 3)
    case Team("Miami Heat") => WLStats(4, 5, 6)
    case _ => WLStats.zero
  }
}
