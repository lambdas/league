package lambdas.league.utils

import cats.Monad
import org.http4s.MediaType.`application/json`
import org.http4s.headers.Accept
import org.http4s.twirl._
import org.http4s.{EntityEncoder, Request, Response}
import play.twirl.api.HtmlFormat

object http {
  implicit class HtmlOps(private val body: HtmlFormat.Appendable) extends AnyVal {
    def ok[F[_]: Monad]: F[Response[F]] = Response[F]().withBody(body)
  }

  implicit class BodyOps[A](private val body: A) extends AnyVal {
    def ok[F[_]: Monad](implicit ev: EntityEncoder[F, A]): F[Response[F]] = Response[F]().withBody(body)
  }

  implicit class RequestOps[F[_]](private val request: Request[F]) extends AnyVal {
    def acceptsJson: Boolean = request.headers.get(Accept).contains(Accept(`application/json`))
  }
}
