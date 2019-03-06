package lambdas.league.models

import io.circe._
import io.circe.generic.semiauto._

case class WLStats(nWins: Int, nLosses: Int, nHidden: Int)

object WLStats {
  val zero: WLStats = WLStats(0, 0, 0)

  implicit val jsonEncoder: Encoder[WLStats] = deriveEncoder
}
