package lambdas.league.models

import anorm.{Column, MetaDataItem, ToStatement, TypeDoesNotMatch}
import io.circe.{Decoder, Encoder, Json}

sealed trait GameType {
  def stringValue: String
}

object GameType {
  case object Regular extends GameType {
    val stringValue = "regular"
  }

  case object Playoff extends GameType {
    val stringValue = "playoff"
  }

  implicit val jsonEncoder: Encoder[GameType] = t => Json.fromString(t.stringValue)

  implicit val jsonDecoder: Decoder[GameType] = _.as[String].map {
    case Regular.stringValue => Regular
    case Playoff.stringValue => Playoff
  }
  
  implicit def column: Column[GameType] = Column.nonNull { (value, meta) =>
    val MetaDataItem(qualified, nullable, clazz) = meta
    value match {
      case Regular.stringValue => Right(Regular)
      case Playoff.stringValue => Right(Playoff)
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.getClass} to GameType for column $qualified"))
    }
  }

  implicit val toStatement: ToStatement[GameType] = ToStatement.of[String].contramap[GameType](_.stringValue)
}

