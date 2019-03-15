package lambdas.league.store

import java.sql.Connection
import java.time.LocalDate

import anorm.Macro.ColumnNaming
import anorm.SqlParser._
import anorm._
import cats.effect.{Resource, Sync}
import lambdas.league.models.GameResult

object DbStore {

  private val parser = Macro.namedParser[GameResult](ColumnNaming.SnakeCase)

  def save[F[_]: Sync](db: Resource[F, Connection], r: GameResult): F[Unit] = db.use { conn =>
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

  def load[F[_]: Sync](db: Resource[F, Connection]): F[List[GameResult]] = db.use { conn =>
    Sync[F].delay {
      SQL"select * from results order by date desc, road_team asc".as(parser.*)(conn)
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

}
