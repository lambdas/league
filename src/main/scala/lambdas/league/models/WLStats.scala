package lambdas.league.models

import cats.instances.map._
import cats.instances.set._
import cats.kernel.CommutativeMonoid
import cats.syntax.foldable._
import io.circe._
import io.circe.generic.semiauto._

final case class WLStats(nWins: Int, nLosses: Int, nHidden: Int)

object WLStats {
  val zero: WLStats = WLStats(0, 0, 0)

  implicit val jsonEncoder: Encoder[WLStats] = deriveEncoder

  implicit val commutativeMonoid: CommutativeMonoid[WLStats] = new CommutativeMonoid[WLStats] {
    def empty: WLStats = zero
    def combine(x: WLStats, y: WLStats): WLStats = WLStats(x.nWins + y.nWins, x.nLosses + y.nLosses, x.nHidden + y.nHidden)
  }

  def single(win: Boolean, visible: Boolean): WLStats =
    WLStats(toInt(visible && win), toInt(visible && !win), toInt(!visible))

  def fromResult(r: GameResult): Map[Team, WLStats] = {
    val roadWin = r.roadScore > r.homeScore
    Map(r.roadTeam -> single(roadWin, r.visible), r.homeTeam -> single(!roadWin, r.visible))
  }

  def fromResults(results: Set[GameResult]): Map[Team, WLStats] = results.unorderedFoldMap(fromResult)

  private def toInt(b: Boolean): Int = if (b) 1 else 0
}
