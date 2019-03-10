package lambdas.league.services

import cats.Monad
import cats.data.Kleisli
import io.circe.syntax._
import lambdas.league.models.GameResult
import lambdas.league.utils.http._
import org.http4s.{HttpService, _}
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._

object ResultsService {

  def apply[F[_]: Monad](getResults: Kleisli[F, Unit, Seq[GameResult]]): HttpService[F] =
    HttpService[F] {
      case req @ GET -> Root if req.acceptsJson => response(getResults, _.asJson)
      case req @ GET -> Root => response(getResults, V.html.results(_))
    }

  def response[F[_]: Monad, A: EntityEncoder[F, ?]](getResults: Kleisli[F, Unit, Seq[GameResult]],
                                                    render: Seq[GameResult] => A): F[Response[F]] =
    getResults
      .flatMapF(render(_).ok[F])
      .run(())
}
