package lambdas.league.scraper

import java.time.LocalDate

import io.circe.parser._
import lambdas.league.models.GameResult
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ScraperSpec extends FlatSpec with Matchers {
  "parse" should "parse response" in {
    parse(Source.fromResource("scoreboard.json").mkString).flatMap(Scraper.parse) shouldBe Right(List(
      GameResult(-1, "Toronto Raptors", "LA Clippers", 123, 99, LocalDate.of(2018, 12, 11), false),
      GameResult(-1, "Phoenix Suns", "San Antonio Spurs", 86, 111, LocalDate.of(2018, 12, 11), false),
      GameResult(-1, "Portland Trail Blazers", "Houston Rockets", 104, 111, LocalDate.of(2018, 12, 11), false)))
  }
}
