package lambdas.league.services

import java.time.LocalDate

import cats.Id
import cats.data.Kleisli
import io.circe.Json
import io.circe.literal._
import lambdas.league.models.GameResult
import lambdas.league.testutils._
import org.http4s.MediaType.{`application/json`, `text/html`}
import org.http4s.circe._
import org.http4s.headers.Accept
import org.http4s.{Headers, Request}
import org.scalatest.{FlatSpec, Matchers}

class ResultsServiceSpec extends FlatSpec with Matchers {
  "Service" should "response with JSON" in {
    val sut = ResultsService(getResults)
    val req = Request[Id](headers = Headers(Accept(`application/json`)))
    val content = sut.run(req).value.get.as[Json]

    content shouldBe json"""
      [
        {
          "roadTeam": "Atlanta Hawks",
          "homeTeam": "Miami Heat",
          "roadScore": 100,
          "homeScore": 110,
          "date": "2019-01-01",
          "visible": true
        },
        {
          "roadTeam": "Atlanta Hawks",
          "homeTeam": "Boston Celtics",
          "roadScore": 105,
          "homeScore": 115,
          "date": "2019-01-02",
          "visible": true
        },
        {
          "roadTeam": "Atlanta Hawks",
          "homeTeam": "Denver Nuggets",
          "roadScore": 120,
          "homeScore": 125,
          "date": "2019-01-02",
          "visible": false
        }
      ]"""
  }

  it should "response with HTML" in {
    val sut = ResultsService(getResults)
    val req = Request[Id](headers = Headers(Accept(`text/html`)))
    val content = sut.run(req).value.get.as[String].replaceAll("\n", " ")

    content should include regex "<tr>.*2019-01-01.*Atlanta Hawks.*100.*110.*Miami Heat.*</tr>"
    content should include regex "<tr>.*2019-01-02.*Atlanta Hawks.*105.*115.*Boston Celtics.*</tr>"
    content should include regex "<tr>.*2019-01-02.*Atlanta Hawks.*\\?.*\\?.*Denver Nuggets.*</tr>"
  }

  private val getResults = Kleisli[Id, Unit, Seq[GameResult]] { _ =>
    Seq(
      GameResult("Atlanta Hawks", "Miami Heat",     100, 110, LocalDate.of(2019, 1, 1), true),
      GameResult("Atlanta Hawks", "Boston Celtics", 105, 115, LocalDate.of(2019, 1, 2), true),
      GameResult("Atlanta Hawks", "Denver Nuggets", 120, 125, LocalDate.of(2019, 1, 2), false))
  }
}
