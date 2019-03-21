package lambdas.league.services

import cats.Monad
import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.instances.list._
import io.circe.syntax._
import lambdas.league.models.{Team, TeamCode, WLStats}
import lambdas.league.utils.http._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.twirl._

object StandingsService {

  def apply[F[_]: Sync](getTeams: Kleisli[F, Unit, List[Team]],
                        getWLStats: Kleisli[F, TeamCode, WLStats]): Kleisli[OptionT[F, ?], Request[F], Response[F]] =
    HttpRoutes.of[F] {
      case req @ GET -> Root if req.acceptsJson => response(getTeams, getWLStats, _.toMap.asJson)
      case GET -> Root => response(getTeams, getWLStats, V.html.standings(_))
    }

  def response[F[_]: Monad, A: EntityEncoder[F, ?]](getTeams: Kleisli[F, Unit, List[Team]],
                                                    getWLStats: Kleisli[F, TeamCode, WLStats],
                                                    render: List[(Team, WLStats)] => A): F[Response[F]] =
    getTeams
      .flatMapF(getWLStats.local[Team](_.code).tapWith(_ -> _).traverse(_))
      .map(render(_).ok[F])
      .run(())
}
