package lambdas.league.store

import java.sql.Connection
import java.time.LocalDate

import anorm.Macro.ColumnNaming
import anorm.SqlParser._
import anorm._
import cats.effect.{Resource, Sync}
import lambdas.league.models
import lambdas.league.models.{Team, TeamCode, Season, WLStats}

object DbStore {

  private[this] val gameResultsParser = Macro.namedParser[models.GameResult](ColumnNaming.SnakeCase)
  private[this] val teamParser = Macro.namedParser[Team](ColumnNaming.SnakeCase)
  private[this] val seasonParser = Macro.namedParser[Season](ColumnNaming.SnakeCase)
  private[this] val wlStatsParser = Macro.namedParser[WLStats](ColumnNaming.SnakeCase)

  def saveResult[F[_]: Sync](db: Resource[F, Connection], r: GameResult): F[Unit] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            insert into results (
              season,
              date,
              game_type,
              road_team,
              home_team,
              road_score,
              home_score
            )
            values (
              ${r.season},
              ${r.date},
              ${r.gameType}::game_type,
              ${r.roadTeam},
              ${r.homeTeam},
              ${r.roadScore},
              ${r.homeScore}
            )
            on conflict do nothing
        """.executeInsert()(conn)
    }
  }

  def results[F[_]: Sync](db: Resource[F, Connection], userId: Long, season: Int): F[List[models.GameResult]] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            select id, season, date, game_type, road_team, home_team, road_score, home_score, coalesce(visible, false) as visible
            from results 
            left outer join user_results
            on (
                  results.id     = user_results.result_id 
              and results.season = $season 
              and user_id        = $userId
            )
            order by 
              date      desc, 
              road_team asc
         """.as(gameResultsParser.*)(conn)
    }
  }

  def wlStats[F[_]: Sync](db: Resource[F, Connection], userId: Long, season: Int): F[List[WLStats]] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            with refined_results as (
              select road_team, home_team, road_score, home_score, coalesce(visible, false) as visible
              from results 
              left outer join user_results 
              on (
                    results.id           = user_results.result_id 
                and results.season       = $season 
                and user_results.user_id = $userId
              )
            )
            select team_code, sum(win) as n_wins, sum(loss) as n_losses, sum(hidden) as n_hidden from (
              select 
                road_team                                                       as team_code,
                case when road_score > home_score and visible then 1 else 0 end as win,
                case when road_score < home_score and visible then 1 else 0 end as loss,
                case when visible                             then 0 else 1 end as hidden
              from refined_results

              union all

              select 
                home_team                                                       as team_code,
                case when road_score < home_score and visible then 1 else 0 end as win,
                case when road_score > home_score and visible then 1 else 0 end as loss,
                case when visible                             then 0 else 1 end as hidden
              from refined_results
            ) x group by team_code
         """.as(wlStatsParser.*)(conn)
    }
  }

  def teams[F[_]: Sync](db: Resource[F, Connection]): F[List[Team]] = db.use { conn =>
    Sync[F].delay {
      SQL"select * from teams order by name asc".as(teamParser.*)(conn)
    }
  }

  def setVisible[F[_]: Sync](db: Resource[F, Connection], userId: Long, resultId: Long): F[Unit] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            insert into user_results (
              user_id, 
              result_id, 
              visible
            )
            values (
              $userId,
              $resultId,
              true
            )
            on conflict(user_id, result_id) do update set visible = true
         """.executeUpdate()(conn)
    }
  }

  def lastDate[F[_]: Sync](db: Resource[F, Connection]): F[Option[LocalDate]] = db.use { conn =>
    Sync[F].delay {
      SQL"select date from results order by date desc limit 1".as(scalar[LocalDate].singleOpt)(conn)
    }
  }

  def seasons[F[_]: Sync](db: Resource[F, Connection]): F[List[Season]] = db.use { conn =>
    Sync[F].delay {
      SQL"select * from seasons order by start_year asc".as(seasonParser.*)(conn)
    }
  }
}
