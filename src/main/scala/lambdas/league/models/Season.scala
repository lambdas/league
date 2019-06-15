package lambdas.league.models

import org.joda.time.LocalDate

final case class Season(startYear: Int,
                        regularSeasonStart: LocalDate,
                        regularSeasonEnd: LocalDate,
                        playoffsStart: LocalDate,
                        playoffsEnd: LocalDate)
