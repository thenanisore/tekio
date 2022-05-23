package me.oeshiro.tekio.services.auth

import me.oeshiro.tekio.domain.auth._
import me.oeshiro.tekio.domain.user.User
import me.oeshiro.tekio.utils.Id
import tsec.authentication.JWTAuthenticator
import tsec.passwordhashers.PasswordHash

trait AuthServiceAlgebra[F[_]] {

  def auth: JWTAuthenticator[F, Id[User], User, JWTSigningKey]

  def hashPassword(password: String, salt: String): F[PasswordHash[HashAlgorithm]]

  def verifyLogin(credentials: Credentials, password: String): F[Unit]

  def generateToken(id: Id[User]): F[String]

  def generateSalt: F[String]
}
