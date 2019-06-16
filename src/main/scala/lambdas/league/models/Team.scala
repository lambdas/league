package lambdas.league.models

import io.circe._
import io.circe.generic.semiauto._

final case class Team(code: TeamCode, name: String, conference: String, division: String)

object Team {
  implicit val jsonEncoder: Encoder[Team] = deriveEncoder
  implicit val keyEncoder: KeyEncoder[Team] = KeyEncoder.instance(_.code)
}
