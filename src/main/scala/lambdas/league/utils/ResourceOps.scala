package lambdas.league.utils

import cats.effect.{Resource, Sync}

class ResourceOps[F[_], A](private val resource: Resource[F, A]) extends AnyVal {
  def useDelay[B](f: A => B)(implicit ev: Sync[F]): F[B] = resource.use(r => Sync[F].delay(f(r)))
}

object ResourceOpsImplicits {
  implicit def toResourceOps[F[_]: Sync, A](resource: Resource[F, A]): ResourceOps[F, A] = new ResourceOps(resource)
}
