package lambdas.league.scraper

import java.time.LocalDate

import io.circe.parser._
import lambdas.league.models.{GameResult, Team}
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ScraperSpec extends FlatSpec with Matchers {
  "parse" should "parse response" in {
    parse(Source.fromResource("scoreboard.json").mkString).flatMap(Scraper.parse) shouldBe Right(List(
      GameResult(Team("Toronto Raptors"), Team("LA Clippers"), 123, 99, LocalDate.of(2018, 12, 11), true),
      GameResult(Team("Phoenix Suns"), Team("San Antonio Spurs"), 86, 111, LocalDate.of(2018, 12, 11), true),
      GameResult(Team("Portland Trail Blazers"), Team("Houston Rockets"), 104, 111, LocalDate.of(2018, 12, 11), true)))
  }
}
