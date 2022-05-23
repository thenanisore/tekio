package me.oeshiro.tekio.services.user

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user.{Profile, Settings, User}
import me.oeshiro.tekio.utils.Id

trait UserServiceAlgebra[F[_]] {


  /**
    * Creates a new user in a repository and returns its id.
    */
  def createUser(email: String, password: String, username: Option[String]): F[Id[User]]

  /**
    * Verifies the given email and password and returns a token.
    */
  def signIn(email: String, password: String): F[String]

  /**
    * Returns a user from the repository by an id.
    */
  def findById(id: Id[User]): F[User]

  /**
    * Returns a user from the repository by an email.
    */
  def findByEmail(email: String): F[User]

  /**
    * Updates user preferences in the repository.
    */
  def updateUserSettings(user: Id[User], updated: Settings): F[Id[User]]

  /**
    * Updates user profile info in the repository.
    */
  def updateUserProfile(user: Id[User], updated: Profile): F[Id[User]]

  /**
    * Returns a user profile from a repository by an id.
    */
  def findProfileById(id: Id[User]): F[Profile]
}
