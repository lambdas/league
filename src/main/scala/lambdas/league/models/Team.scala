package lambdas.league.models

import io.circe._
import io.circe.generic.semiauto._

sealed case class Team(name: String)

object Team {
  implicit val jsonEncoder: Encoder[Team] = deriveEncoder
  implicit val jsonKeyEncoder: KeyEncoder[Team] = KeyEncoder.instance(_.name)
}
