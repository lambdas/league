package lambdas.league.utils

import cats.{Applicative, Monad}
import org.http4s.MediaType.`application/json`
import org.http4s.headers.{Accept, Location}
import org.http4s.{EntityEncoder, Headers, Request, Response, Status, Uri}

object http {
  implicit class BodyOps[A](private val body: A) extends AnyVal {
    def ok[F[_]: Monad](implicit ev: EntityEncoder[F, A]): F[Response[F]] = Response[F]().withBody(body)
    def seeOther[F[_]: Monad](implicit ev: A =:= Uri): F[Response[F]] = Applicative[F].pure(Response[F](status = Status.SeeOther, headers = Headers(Location(body))))
  }

  implicit class RequestOps[F[_]](private val request: Request[F]) extends AnyVal {
    def acceptsJson: Boolean = request.headers.get(Accept).contains(Accept(`application/json`))
  }
}
