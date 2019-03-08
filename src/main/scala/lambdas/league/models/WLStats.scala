package lambdas.league.models

import cats.instances.map._
import cats.instances.set._
import cats.kernel.CommutativeMonoid
import cats.syntax.foldable._
import io.circe._
import io.circe.generic.semiauto._

sealed case class WLStats(nWins: Int, nLosses: Int, nHidden: Int)

object WLStats {
  def apply(win: Boolean, hidden: Boolean): WLStats =
    WLStats(toInt(!hidden && win), toInt(!hidden && !win), toInt(hidden))

  def fromResult(r: GameResult): Map[Team, WLStats] = {
    val roadWin = r.roadScore > r.homeScore
    Map(r.roadTeam -> WLStats(roadWin, r.hidden), r.homeTeam -> WLStats(!roadWin, r.hidden))
  }

  def fromResults(results: Set[GameResult]): Map[Team, WLStats] = results.unorderedFoldMap(fromResult)

  val zero: WLStats = WLStats(0, 0, 0)

  implicit val jsonEncoder: Encoder[WLStats] = deriveEncoder

  implicit val commutativeMonoid: CommutativeMonoid[WLStats] = new CommutativeMonoid[WLStats] {
    def empty: WLStats = zero
    def combine(x: WLStats, y: WLStats): WLStats = WLStats(x.nWins + y.nWins, x.nLosses + y.nLosses, x.nHidden + y.nHidden)
  }

  private def toInt(b: Boolean): Int = if (b) 1 else 0
}
