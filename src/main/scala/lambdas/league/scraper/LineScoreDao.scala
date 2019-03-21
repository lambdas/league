package lambdas.league.scraper

import cats.instances.either._
import cats.syntax.apply._
import io.circe.{Decoder, HCursor}

private[scraper] final case class LineScoreDao(gameSeq: Int, teamId: Long, teamCode: String, score: Int)

private[scraper] object LineScoreDao {
  implicit val jsonDecoder: Decoder[LineScoreDao] = Decoder { c: HCursor =>
    (
      c.downN(1).as[Int],
      c.downN(3).as[Long],
      c.downN(4).as[String],
      c.downN(22).as[Int]
    ).mapN(LineScoreDao.apply)
  }
}
