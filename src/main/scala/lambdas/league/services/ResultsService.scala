package lambdas.league.services

import cats.Monad
import cats.data.Kleisli
import io.circe.syntax._
import cats.syntax.flatMap._
import lambdas.league.models.GameResult
import lambdas.league.utils.http._
import org.http4s.{HttpService, _}
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._

object ResultsService {

  def apply[F[_]: Monad](getResults: Kleisli[F, Unit, Seq[GameResult]],
                         setResultVisible: Kleisli[F, Long, Unit]): HttpService[F] =
    HttpService[F] {
      case req @ GET -> Root if req.acceptsJson => results(getResults, _.asJson)
      case GET -> Root / "results" => results(getResults, V.html.results(_))
      case POST -> Root / "result" / LongVar(id) / "show" => show(setResultVisible, id)
    }

  def results[F[_]: Monad, A: EntityEncoder[F, ?]](getResults: Kleisli[F, Unit, Seq[GameResult]],
                                                   render: Seq[GameResult] => A): F[Response[F]] = {
    getResults
      .flatMapF(render(_).ok[F])
      .run(())
  }

  def show[F[_]: Monad](setResultVisible: Kleisli[F, Long, Unit], resultId: Long): F[Response[F]] = {
    setResultVisible.run(resultId).flatMap { _ => Uri.unsafeFromString("/results").seeOther }
  }
}
