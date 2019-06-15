package lambdas.league.store

import java.sql.Connection
import java.time.LocalDate

import anorm.Macro.ColumnNaming
import anorm.SqlParser._
import anorm._
import cats.effect.{Resource, Sync}
import lambdas.league.models.{GameResult, Team, Season}

object DbStore {

  private val parser = Macro.namedParser[GameResult](ColumnNaming.SnakeCase)
  private val teamParser = Macro.namedParser[Team](ColumnNaming.SnakeCase)
  private val seasonParser = Macro.namedParser[Season](ColumnNaming.SnakeCase)

  def saveResult[F[_]: Sync](db: Resource[F, Connection], r: GameResult): F[Unit] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            insert into results (
              date,
              road_team,
              home_team,
              road_score,
              home_score,
              visible
            ) values (
              ${r.date},
              ${r.roadTeam},
              ${r.homeTeam},
              ${r.roadScore},
              ${r.homeScore},
              ${r.visible}) on conflict do nothing
        """.executeInsert()(conn)
    }
  }

  def results[F[_]: Sync](db: Resource[F, Connection], seasonStartYear: Int): F[List[GameResult]] = db.use { conn =>
    Sync[F].delay {
      SQL"""
            select * from results 
            where date >= (select regular_season_start from seasons where start_year = $seasonStartYear) 
            and   date <= (select regular_season_end   from seasons where start_year = $seasonStartYear) 
            order by date desc, road_team asc
         """.as(parser.*)(conn)
    }
  }

  def teams[F[_]: Sync](db: Resource[F, Connection]): F[List[Team]] = db.use { conn =>
    Sync[F].delay {
      SQL"select * from teams order by name asc".as(teamParser.*)(conn)
    }
  }

  def setVisible[F[_]: Sync](db: Resource[F, Connection], id: Long): F[Unit] = db.use { conn =>
    Sync[F].delay {
      SQL"update results set visible=true where id=$id".executeUpdate()(conn)
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
