package lambdas.league.models

import java.time.LocalDate

import cats.syntax.monoid._
import io.circe.literal._
import io.circe.syntax._
import lambdas.league.models.WLStats.{fromResult, zero}
import org.scalatest.{FlatSpec, Matchers}

class WLStatsSpec extends FlatSpec with Matchers {
  "WLStats" should "construct stats from win(true) and hidden" in {
    WLStats(false, false) shouldBe WLStats(0, 1, 0)
    WLStats(true, false) shouldBe WLStats(1, 0, 0)
    WLStats(false, true) shouldBe WLStats(0, 0, 1)
    WLStats(true, true) shouldBe WLStats(0, 0, 1)
  }

  "fromResult" should "construct stats from result" in {
    fromResult(
      GameResult(
        Team("Atlanta Hawks"),
        Team("Miami Heat"),
        100,
        110,
        LocalDate.of(2019, 1, 1),
        false)) shouldBe Map(
      Team("Atlanta Hawks") -> WLStats(0, 1, 0),
      Team("Miami Heat") -> WLStats(1, 0, 0))
  }

  it should "construct stats from result when hidden" in {
    fromResult(
      GameResult(
        Team("Atlanta Hawks"),
        Team("Miami Heat"),
        100,
        110,
        LocalDate.of(2019, 1, 1),
        true)) shouldBe Map(
      Team("Atlanta Hawks") -> WLStats(0, 0, 1),
      Team("Miami Heat") -> WLStats(0, 0, 1))
  }

  "zero" should "provide null stats" in {
    zero shouldBe WLStats(0, 0, 0)
  }

  "WLStats" should "serialize in valid json" in {
    WLStats(10, 20, 5).asJson shouldBe json"""
      {
        "nWins": 10,
        "nLosses": 20,
        "nHidden": 5
      }"""
  }

  it should "form commutative monoid" in {
    val a = WLStats(1, 2, 3)
    val b = WLStats(6, 5, 4)
    val c = WLStats(8, 7, 9)

    a |+| zero shouldBe a
    a |+| b shouldBe b |+| a
    a |+| (b |+| c) shouldBe (a |+| b) |+| c
  }
}
