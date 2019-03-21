package lambdas.league.models

import io.circe._
import io.circe.generic.semiauto._

case class Team(code: TeamName, name: String, conference: String, division: String)

object Team {
  implicit val jsonEncoder: Encoder[Team] = deriveEncoder
  implicit val keyEncoder: KeyEncoder[Team] = KeyEncoder.instance(_.code)
}
