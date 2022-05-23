package me.oeshiro.tekio.services.user

import cats.MonadError
import cats.effect.Clock
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.config.UserConfig
import me.oeshiro.tekio.domain.auth.Credentials
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.external.user.UserRepositoryAlgebra
import me.oeshiro.tekio.services.auth.AuthServiceAlgebra
import me.oeshiro.tekio.services.item.ItemServiceAlgebra
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.ClockUtils._

import scala.concurrent.duration._

class UserService[F[_]: Logger: Clock](
    config: UserConfig,
    authService: AuthServiceAlgebra[F],
    itemService: ItemServiceAlgebra[F],
    userRepo: UserRepositoryAlgebra[F]
)(implicit F: MonadError[F, Throwable])
    extends UserServiceAlgebra[F] {

  /**
    * Creates a new user in a repository and returns its id.
    */
  override def createUser(email: String, password: String, username: Option[String]): F[Id[User]] =
    for {
      _ <- findByEmail(email).attempt >>= {
        case Right(_) => F.raiseError[Unit](UserAlreadyExists(s"email=$email"))
        case Left(_)  => F.unit
      }
      salt <- authService.generateSalt
      hash <- authService.hashPassword(password, salt)
      uname = username.filterNot(_.isEmpty).getOrElse(email.split('@').head)
      user  = NewUser(email, hash, salt, uname, config.defaults)
      _  <- Logger[F].info(s"Creating a new user with email=$email, username=$username")
      id <- userRepo.save(user)
      _  <- Logger[F].info(s"Creating user kanji for new user id=$id")
      _  <- itemService.createUserKanji(id)
      _  <- Logger[F].info(s"Setting initial kanji order: ${config.defaults.preferredOrder}")
      _  <- itemService.setKanjiOrder(id, config.defaults)
    } yield id

  /**
    * Verifies the given email and password and returns a token.
    */
  override def signIn(email: String, password: String): F[String] =
    for {
      creds <- findCredentialsByEmail(email)
      _     <- authService.verifyLogin(creds, password)
      jwt   <- authService.generateToken(creds.userId)
      _     <- Logger[F].info(s"User $email successfully signed in")
    } yield jwt

  /**
    * Returns a user from a repository by an id.
    */
  override def findById(id: Id[User]): F[User] =
    userRepo.get(id).value.flatMap {
      case Some(user) => obtainUserInfo(user)
      case None       => F.raiseError(UserNotFound(s"id=$id"))
    }

  /**
    * Returns a user from a repository by an id.
    */
  override def findByEmail(email: String): F[User] =
    userRepo.getByEmail(email).value.flatMap {
      case Some(user) => obtainUserInfo(user)
      case None       => F.raiseError(UserNotFound(s"email=$email"))
    }

  private[user] def obtainUserInfo(user: User): F[User] =
    for {
      _                           <- Logger[F].info(s"Constructing user info for user=${user.id}")
      unlocked                    <- itemService.getUserKanji(user.id, unlockedOption = true.some)
      startOfDay                  <- Clock[F].startOfDay
      (spentToday, unlockedToday) <- itemService.spentFrom(user.id, from = startOfDay)
      _                           <- Logger[F].info(s"BALANCING: User id=${user.id} email=${user.email} " +
                                                    s"spent ~${spentToday.toSeconds}s today, unlocked $unlockedToday kanji")
      reviewQueue <- itemService.getReviewQueue(
        user = user.id,
        time = (user.settings.minutesPerDay.minutes - spentToday).some,
        maxSize = user.settings.maxReviewSize.some
      )
      lessonTime = user.settings.minutesPerDay.minutes - spentToday
      maxLessonsLeft = user.settings.maxLessonSize - unlockedToday
      lessonQueue <- itemService.getLessonQueue(user.id, lessonTime.some, maxLessonsLeft, user.settings)
    } yield user.copy(unlocked = unlocked.map(_.id), lessonQueue = lessonQueue, reviewQueue = reviewQueue)

  /**
    * Returns a user from a repository by an id.
    */
  private def findCredentialsByEmail(email: String): F[Credentials] =
    userRepo.getCredentials(email).value.flatMap {
      case Some(creds) => creds.pure[F]
      case None        => F.raiseError(UserNotFound(s"email=$email"))
    }

  /**
    * Updates user preferences in the repository.
    */
  override def updateUserSettings(user: Id[User], updated: Settings): F[Id[User]] =
    userRepo.putSettings(user, updated)

  /**
    * Updates user profile info in the repository.
    */
  override def updateUserProfile(user: Id[User], updated: Profile): F[Id[User]] =
    for {
      _  <- validateProfile(updated)
      id <- userRepo.putProfile(user, updated)
    } yield id

  private[user] def validateProfile(profile: Profile): F[Unit] =
    if (profile.username.isEmpty) F.raiseError(InvalidProfile("username cannot be empty"))
    else F.unit

  /**
    * Returns a user profile from a repository by an id.
    */
  override def findProfileById(id: Id[User]): F[Profile] =
    userRepo.getProfile(id).value.flatMap {
      case Some(profile) => profile.pure[F]
      case None          => F.raiseError(UserNotFound(s"id=$id"))
    }

}
