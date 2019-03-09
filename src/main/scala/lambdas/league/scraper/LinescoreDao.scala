package lambdas.league.scraper

import cats.instances.either._
import cats.syntax.apply._
import io.circe.{Decoder, HCursor}

private[scraper] final case class LinescoreDao(gameSeq: Int, teamId: Long, city: String, name: String, score: Int)

private[scraper] object LinescoreDao {
  implicit val jsonDecoder: Decoder[LinescoreDao] = Decoder { c: HCursor =>
    (
      c.downN(1).as[Int],
      c.downN(3).as[Long],
      c.downN(5).as[String],
      c.downN(6).as[String],
      c.downN(22).as[Int]
    ).mapN(LinescoreDao.apply _)
  }
}
