package lambdas.league.services

import cats.Monad
import cats.data.Kleisli
import cats.instances.list._
import io.circe.syntax._
import lambdas.league.models.{Team, WLStats}
import lambdas.league.utils.http._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._

object TeamsService {

  def apply[F[_]: Monad](getTeams: Kleisli[F, Unit, Set[Team]],
                         getWLStats: Kleisli[F, Team, WLStats]): HttpService[F] =
    HttpService[F] {
      case req @ GET -> Root if req.acceptsJson => response(getTeams, getWLStats, _.asJson)
      case GET -> Root => response(getTeams, getWLStats, V.html.league(_))
    }

  def response[F[_]: Monad, A: EntityEncoder[F, ?]](getTeams: Kleisli[F, Unit, Set[Team]],
                                                    getWLStats: Kleisli[F, Team, WLStats],
                                                    render: Map[Team, WLStats] => A): F[Response[F]] =
    getTeams
      .map(_.toList)
      .flatMapF(getWLStats.tapWith(_ -> _).traverse(_))
      .map(_.toMap)
      .flatMapF(render(_).ok[F])
      .run(())
}
