package lambdas.league.models

import cats.Eq
import cats.instances.string._
import io.circe._
import io.circe.generic.semiauto._

final case class Team(name: String)

object Team {
  implicit val jsonEncoder: Encoder[Team] = deriveEncoder
  implicit val jsonKeyEncoder: KeyEncoder[Team] = KeyEncoder.instance(_.name)
  implicit val eq: Eq[Team] = Eq.by[Team, String](_.name)
}
