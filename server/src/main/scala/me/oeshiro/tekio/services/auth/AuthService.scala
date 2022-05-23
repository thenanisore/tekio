package me.oeshiro.tekio.services.auth

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._
import me.oeshiro.tekio.config.AuthConfig
import me.oeshiro.tekio.domain.auth.{Credentials, Hash, JWTSigningKey, TekioHasher}
import me.oeshiro.tekio.domain.user.{User, UserAuthenticationFailed}
import me.oeshiro.tekio.external.user.UserRepositoryAlgebra
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import org.bouncycastle.util.encoders.Hex
import tsec.authentication.JWTAuthenticator
import tsec.common.{ManagedRandom, VerificationFailed, Verified}
import tsec.jws.mac.JWTMac
import tsec.mac.jca.MacSigningKey

import scala.concurrent.duration._

class AuthService[F[_]: Sync](
    config: AuthConfig,
    hasher: TekioHasher[F],
    signingKey: MacSigningKey[JWTSigningKey],
    userRepository: UserRepositoryAlgebra[F]
) extends AuthServiceAlgebra[F] with ManagedRandom {

  private[auth] val saltSize = 32

  override val auth: JWTAuthenticator[F, Id[User], User, JWTSigningKey] =
    JWTAuthenticator.unbacked.inBearerToken(
      expiryDuration = config.jwtExpireSeconds.seconds,
      maxIdle = None,
      identityStore = userRepository,
      signingKey = signingKey
    )

  override def hashPassword(password: String, salt: String): F[Hash] = {
    hasher.hashpw(password + salt)
  }

  override def verifyLogin(credentials: Credentials, password: String): F[Unit] =
    for {
      verification <- hasher.checkpw(password + credentials.salt, credentials.hash)
      _ <- verification match {
        case Verified => Sync[F].unit
        case VerificationFailed =>
          Sync[F].raiseError[Unit](UserAuthenticationFailed("hashes don't match"))
      }
    } yield ()

  override def generateToken(id: Id[User]): F[String] =
    auth.create(id).map(jwt => JWTMac.toEncodedString[F, JWTSigningKey](jwt.jwt))

  override def generateSalt: F[String] =
    Sync[F].delay {
      val saltBytes = new Array[Byte](saltSize)
      nextBytes(saltBytes)
      Hex.toHexString(saltBytes)
    }
}
