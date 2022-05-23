package me.oeshiro.tekio.utils

import cats.{ApplicativeError, Monad, MonadError}
import cats.data.NonEmptyList
import cats.effect.Sync
import io.chrisdavenport.log4cats.Logger
import javax.crypto.spec.SecretKeySpec
import me.oeshiro.tekio.domain.auth.JWTSigningKey
import me.oeshiro.tekio.domain.user.{User, UserNotAuthorized, UserNotFound}
import org.http4s.{Challenge, Response, headers}
import tsec.authentication.{AugmentedJWT, SecuredRequest, SecuredRequestHandler, TSecAuthService}
import tsec.common._
import tsec.mac.jca.MacSigningKey

object AuthUtils {

  type AuthType             = AugmentedJWT[JWTSigningKey, Id[User]]
  type AuthRequest[F[_]]    = SecuredRequest[F, User, AuthType]
  type AuthHandler[F[_]]    = SecuredRequestHandler[F, Id[User], User, AuthType]
  type ProtectedRoute[F[_]] = TSecAuthService[User, AuthType, F]

  def protect[F[_]: Monad](pf: => PartialFunction[AuthRequest[F], F[Response[F]]]): ProtectedRoute[F] =
    TSecAuthService.apply(pf)

  val wwwHeader = headers.`WWW-Authenticate`(NonEmptyList.of(
    Challenge(
      scheme = "Bearer",
      realm = "Get a valid token from /user/sign_in",
    )
  ))

  def validateUser[F[_]: Sync](validation: => Boolean): F[Unit] = {
    if (!validation)
      Sync[F].raiseError(UserNotAuthorized())
    else
      Sync[F].unit
  }

  def generateSigningKey[F[_]: Sync](key: String): F[MacSigningKey[JWTSigningKey]] =
    Sync[F].delay {
      val algorithm = "HmacSHA256"
      val keySpec   = new SecretKeySpec(key.utf8Bytes, algorithm)
      MacSigningKey.fromJavaKey[JWTSigningKey](keySpec)
    }
}
