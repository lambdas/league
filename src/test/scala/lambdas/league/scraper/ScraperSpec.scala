package lambdas.league.scraper

import java.time.LocalDate

import io.circe.parser._
import org.scalatest.{FlatSpec, Matchers}

import scala.io.Source

class ScraperSpec extends FlatSpec with Matchers {
  "parse" should "parse response" in {
    parse(Source.fromResource("scoreboard.json").mkString).flatMap(Scraper.parse) shouldBe Right(List(
      GameResult(LocalDate.of(2018, 12, 11), "TOR", "LAC", 123,  99),
      GameResult(LocalDate.of(2018, 12, 11), "PHX", "SAS", 86,  111),
      GameResult(LocalDate.of(2018, 12, 11), "POR", "HOU", 104, 111)))
  }
}
