package lambdas.league.store

import java.time.LocalDate
import lambdas.league.models.{GameType, TeamCode}
import lambdas.league.scraper

/** A result ready to be saved to DB. */
final case class GameResult(season: Int,
                            date: LocalDate,
                            gameType: GameType,
                            roadTeam: TeamCode,
                            homeTeam: TeamCode,
                            roadScore: Int,
                            homeScore: Int)

object GameResult {
  def fromScraper(r: scraper.GameResult, season: Int, gameType: GameType): GameResult = 
    GameResult(season, r.date, gameType, r.roadTeam, r.homeTeam, r.roadScore, r.homeScore)
}
