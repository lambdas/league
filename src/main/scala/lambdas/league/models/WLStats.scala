package lambdas.league.models

import cats.instances.map._
import cats.instances.set._
import cats.kernel.CommutativeMonoid
import cats.syntax.foldable._
import io.circe._
import io.circe.generic.semiauto._

final case class WLStats(teamCode: TeamCode, 
                         nWins: Int, 
                         nLosses: Int, 
                         nHidden: Int)

object WLStats {
  implicit val jsonEncoder: Encoder[WLStats] = deriveEncoder
}
