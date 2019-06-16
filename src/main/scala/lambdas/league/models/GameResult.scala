package lambdas.league.models

import io.circe._
import io.circe.generic.semiauto._
import java.time.LocalDate
import lambdas.league.store

final case class GameResult(id: Long,
                            date: LocalDate,
                            roadTeam: TeamCode,
                            homeTeam: TeamCode,
                            roadScore: Int,
                            homeScore: Int,
                            visible: Boolean)

object GameResult {
  implicit val jsonEncoder: Encoder[GameResult] = deriveEncoder
}
