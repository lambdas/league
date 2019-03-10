package lambdas.league.store

import java.sql.{Connection, DriverManager}
import java.util.Properties

import anorm.Macro.ColumnNaming
import anorm._
import cats.ApplicativeError
import cats.effect.Sync
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.flatMap._
import lambdas.league.models.GameResult

object DbStore {

  private val parser = Macro.namedParser[GameResult](ColumnNaming.SnakeCase)

  def save[F[_]: Sync](r: GameResult): F[Unit] = withConnection { implicit conn =>
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
            ${r.visible})
      """.executeInsert()
  }

  def load[F[_]: Sync]: F[List[GameResult]] = withConnection { implicit conn =>
    SQL"select * from results".as(parser.*)
  }

  private def withConnection[F[_]: Sync, A](f: Connection => A): F[A] = {
    for {
      conn <- openConnection
      result <- Sync[F].delay(f(conn)).attempt
      _ <- closeConnection(conn)
      value <- ApplicativeError[F, Throwable].fromEither(result)
    } yield value
  }

  private def openConnection[F[_]: Sync]: F[Connection] = Sync[F].delay {
    val props = new Properties
    props.setProperty("user", "postgres")
    props.setProperty("password", "whatevs")
    props.setProperty("ssl", "false")

    DriverManager.getConnection("jdbc:postgresql://localhost:5432/league", props)
  }

  private def closeConnection[F[_]: Sync](conn: Connection): F[Unit] = Sync[F].delay {
    conn.close()
  }
}
