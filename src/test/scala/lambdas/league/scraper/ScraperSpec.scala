package lambdas.league.scraper

import java.time.LocalDate

import io.circe.parser._
import lambdas.league.models.GameResult
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ScraperSpec extends FlatSpec with Matchers {
  "parse" should "parse response" in {
    parse(Source.fromResource("scoreboard.json").mkString).flatMap(Scraper.parse) shouldBe Right(List(
      GameResult(-1, "TOR", "LAC", 123, 99, LocalDate.of(2018, 12, 11), false),
      GameResult(-1, "PHX", "SAS", 86, 111, LocalDate.of(2018, 12, 11), false),
      GameResult(-1, "POR", "HOU", 104, 111, LocalDate.of(2018, 12, 11), false)))
  }
}
