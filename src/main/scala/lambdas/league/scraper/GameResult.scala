package lambdas.league.scraper

import java.time.LocalDate
import lambdas.league.models.TeamCode

/** A result coming from scraper. */
final case class GameResult(date: LocalDate,
                            roadTeam: TeamCode,
                            homeTeam: TeamCode,
                            roadScore: Int,
                            homeScore: Int)

