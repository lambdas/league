package lambdas.league.models

import java.time.LocalDate
import io.circe._
import io.circe.generic.semiauto._

final case class GameResult(id: Long,
                            roadTeam: TeamCode,
                            homeTeam: TeamCode,
                            roadScore: Int,
                            homeScore: Int,
                            date: LocalDate,
                            visible: Boolean)

object GameResult {
  implicit val jsonEncoder: Encoder[GameResult] = deriveEncoder
}
