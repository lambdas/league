package lambdas.league.utils

import cats.data.{Kleisli, OptionT}
import cats.{Applicative, Monad}
import org.http4s.MediaType.application
import org.http4s.headers.{Accept, Location}
import org.http4s.{EntityEncoder, Headers, Request, Response, Status, Uri}

object http {
  implicit final class BodyOps[A](private val body: A) extends AnyVal {
    def ok[F[_]: Monad](implicit ev: EntityEncoder[F, A]): Response[F] = Response[F]().withEntity(body)
    def seeOther[F[_]: Monad](implicit ev: A =:= Uri): F[Response[F]] = Applicative[F].pure(Response[F](status = Status.SeeOther, headers = Headers(Location(body))))
  }

  implicit final class RequestOps[F[_]](private val request: Request[F]) extends AnyVal {
    def acceptsJson: Boolean = request.headers.get(Accept).contains(Accept(application.json))
  }

  implicit final class KleisliResponseOps[F[_]: Monad, A](self: Kleisli[OptionT[F, ?], A, Response[F]]) {
    def and(next: Kleisli[OptionT[F, ?], A, Response[F]]): Kleisli[OptionT[F, ?], A, Response[F]] =
      Kleisli(a => self.run(a).orElse(next.run(a)))
  }
}
