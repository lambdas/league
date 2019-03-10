package lambdas.league.models

import java.time.LocalDate

final case class GameResult(roadTeam: Team,
                            homeTeam: Team,
                            roadScore: Int,
                            homeScore: Int,
                            date: LocalDate,
                            visible: Boolean)
