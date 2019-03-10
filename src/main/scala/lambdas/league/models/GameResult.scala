package lambdas.league.models

import java.time.LocalDate
import io.circe._
import io.circe.generic.semiauto._

final case class GameResult(roadTeam: Team,
                            homeTeam: Team,
                            roadScore: Int,
                            homeScore: Int,
                            date: LocalDate,
                            visible: Boolean)

object GameResult {
  implicit val jsonEncoder: Encoder[GameResult] = deriveEncoder
}
