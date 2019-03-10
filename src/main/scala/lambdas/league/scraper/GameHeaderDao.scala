package lambdas.league.scraper

import java.time.{LocalDate, LocalDateTime}

import cats.instances.either._
import cats.syntax.apply._
import io.circe.{Decoder, HCursor}

private[scraper] final case class GameHeaderDao(gameSeq: Int, date: LocalDate, roadTeamId: Long, homeTeamId: Long)

private[scraper] object GameHeaderDao {
  implicit val jsonDecoder: Decoder[GameHeaderDao] = { c: HCursor =>
    (
      c.downN(1).as[Int],
      c.downN(0).as[LocalDateTime].map(_.toLocalDate),
      c.downN(7).as[Long],
      c.downN(6).as[Long]
    ).mapN(GameHeaderDao.apply)
  }
}
