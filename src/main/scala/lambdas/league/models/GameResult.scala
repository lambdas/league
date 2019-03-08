package lambdas.league.models

import java.time.LocalDate

sealed case class GameResult(roadTeam: Team,
                             homeTeam: Team,
                             roadScore: Int,
                             homeScore: Int,
                             date: LocalDate,
                             hidden: Boolean)
