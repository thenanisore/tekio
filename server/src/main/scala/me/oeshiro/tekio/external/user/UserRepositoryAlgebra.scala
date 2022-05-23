package me.oeshiro.tekio.external.user

import cats.data.OptionT
import me.oeshiro.tekio.domain.auth.Credentials
import me.oeshiro.tekio.domain.user.{NewUser, Profile, Settings, User}
import me.oeshiro.tekio.utils.Id
import tsec.authentication.IdentityStore

trait UserRepositoryAlgebra[F[_]] extends IdentityStore[F, Id[User], User] {

  def get(id: Id[User]): OptionT[F, User]

  def getByEmail(email: String): OptionT[F, User]

  def getCredentials(email: String): OptionT[F, Credentials]

  def getAll: F[Seq[User]]

  def save(user: NewUser): F[Id[User]]

  def putSettings(user: Id[User], updated: Settings): F[Id[User]]

  def getProfile(id: Id[User]): OptionT[F, Profile]

  def putProfile(user: Id[User], updated: Profile): F[Id[User]]
}
