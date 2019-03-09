package lambdas.league.scraper

import cats.syntax.apply._
import cats.instances.either._
import io.circe.{Decoder, DecodingFailure, HCursor, JsonObject}

private[scraper] final case class ResponseDao(gameHeaders: List[GameHeaderDao],
                                              lineScores: List[LinescoreDao])

private[scraper] object ResponseDao {
  implicit val jsonDecoder: Decoder[ResponseDao] = Decoder { c: HCursor =>
    c.get[List[JsonObject]]("resultSets")
      .flatMap { rs =>
        (
          rs.find(
            _("name")
              .toRight(DecodingFailure("name is not a string", c.history))
              .flatMap(_.as[String])
              .contains("GameHeader"))
          .toRight(DecodingFailure("no object with name GameHeader", c.history))
          .flatMap(_("rowSet").toRight(DecodingFailure("no rowSet", c.history)))
          .flatMap(_.as[List[GameHeaderDao]]),
          rs.find(
            _("name")
              .toRight(DecodingFailure("name is not a string", c.history))
              .flatMap(_.as[String])
              .contains("LineScore"))
            .toRight(DecodingFailure("no object with name LineScore", c.history))
            .flatMap(_("rowSet").toRight(DecodingFailure("no rowSet", c.history)))
            .flatMap(_.as[List[LinescoreDao]])
        ).mapN(ResponseDao.apply _)
      }
  }
}
