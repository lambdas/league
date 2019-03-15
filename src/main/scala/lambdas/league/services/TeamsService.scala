package lambdas.league.services

import cats.Monad
import cats.data.Kleisli
import cats.effect.Sync
import cats.instances.list._
import io.circe.syntax._
import lambdas.league.models.{Team, WLStats}
import lambdas.league.utils.http._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._
import org.http4s.syntax._
import org.http4s.implicits._

object TeamsService {

  def apply[F[_]: Sync](getTeams: Kleisli[F, Unit, List[Team]],
                        getWLStats: Kleisli[F, Team, WLStats]): Kleisli[F, Request[F], Response[F]] =
    HttpRoutes.of[F] {
      case req @ GET -> Root if req.acceptsJson => response(getTeams, getWLStats, _.asJson)
      case GET -> Root => response(getTeams, getWLStats, V.html.league(_))
    }.orNotFound

  def response[F[_]: Monad, A: EntityEncoder[F, ?]](getTeams: Kleisli[F, Unit, List[Team]],
                                                    getWLStats: Kleisli[F, Team, WLStats],
                                                    render: List[(Team, WLStats)] => A): F[Response[F]] =
    getTeams
      .flatMapF(getWLStats.tapWith(_ -> _).traverse(_))
      .map(render(_).ok[F])
      .run(())
}
