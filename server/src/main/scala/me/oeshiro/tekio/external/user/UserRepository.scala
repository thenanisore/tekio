package me.oeshiro.tekio.external.user

import cats.data.OptionT
import cats.effect.{Clock, Resource, Sync}
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.domain.auth.{Credentials, Hash, HashAlgorithm}
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.external.DTOConverters
import me.oeshiro.tekio.external.DTOConverters.UserRow
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.LoggableEffectOps._
import tsec.passwordhashers.PasswordHash

class UserRepository[F[_]: Logger](xa: Transactor[F])(implicit F: Sync[F]) extends UserRepositoryAlgebra[F] {

  implicit val hashMeta: Meta[Hash] = Meta[String].imap(PasswordHash.apply[HashAlgorithm])(_.toString)

  implicit val orderTypeMeta: Meta[OrderType] =
    Meta[String].imap(OrderType.withNameInsensitive)(_.entryName.toUpperCase)

  override def get(id: Id[User]): OptionT[F, User] = OptionT {
    sql"""
      | select id, email, username, picture, created_at, updated_at,
      | mins_per_day, pref_order, adapt_order, adapt_schedule,
      | batch_size, auto_reveal, max_review_size, max_lesson_size
      | from users
      | where id = ${id.toInt}
    """.stripMargin
      .query[UserRow]
      .option
      .transact(xa)
      .log(user => s"Fetched by id=$id: $user")
      .map(_.map(DTOConverters.toUser))
  }

  override def getByEmail(email: String): OptionT[F, User] = OptionT {
    sql"""
      | select id, email, username, picture, created_at, updated_at,
      | mins_per_day, pref_order, adapt_order, adapt_schedule,
      | batch_size, auto_reveal, max_review_size, max_lesson_size
      | from users
      | where email = $email
    """.stripMargin
      .query[UserRow]
      .option
      .transact(xa)
      .log(user => s"Fetched by email=$email: $user")
      .map(_.map(DTOConverters.toUser))
  }

  def getCredentials(email: String): OptionT[F, Credentials] = OptionT {
    sql"""
       | select id, email, hash, salt
       | from users
       | where email = $email
    """.stripMargin
      .query[Credentials]
      .option
      .transact(xa)
      .log(creds => s"Fetched credentials by email=$email: $creds")
  }

  override def save(user: NewUser): F[Id[User]] =
    sql"""
      | insert into users (
      |   email, hash, salt, username,
      |   mins_per_day, pref_order,
      |   adapt_order, adapt_schedule,
      |   batch_size, auto_reveal, max_review_size, max_lesson_size
      | ) values (
      |   ${user.email}, ${user.hash}, ${user.salt}, ${user.username},
      |   ${user.defaults.minutesPerDay}, ${user.defaults.preferredOrder}::order_type,
      |   ${user.defaults.adaptiveOrder}, ${user.defaults.adaptiveScheduling},
      |   ${user.defaults.batchSize}, ${user.defaults.autoReveal},
      |   ${user.defaults.maxReviewSize}, ${user.defaults.maxLessonSize}
      | )
    """.stripMargin.update
      .withUniqueGeneratedKeys[(Long, String)]("id", "email")
      .transact(xa)
      .log { case (id, email) => s"Successfully inserted user id=$id, email=$email" }
      .map { case (id, _) => Id[User](id.toString) }

  override def getAll: F[Seq[User]] =
    sql"""
         | select id, email, username, picture, created_at, updated_at,
         | mins_per_day, pref_order, adapt_order, adapt_schedule,
         | batch_size, auto_reveal, max_review_size, max_lesson_size
         | from users
    """.stripMargin
      .query[UserRow]
      .to[Seq]
      .transact(xa)
      .map(_.map(DTOConverters.toUser))

  def putSettings(user: Id[User], updated: Settings): F[Id[User]] = {
    sql"""
         | update users
         | set mins_per_day = ${updated.minutesPerDay},
         | pref_order = ${updated.preferredOrder}::order_type,
         | adapt_order = ${updated.adaptiveOrder},
         | adapt_schedule = ${updated.adaptiveScheduling},
         | batch_size = ${updated.batchSize},
         | auto_reveal = ${updated.autoReveal},
         | max_review_size = ${updated.maxReviewSize},
         | max_lesson_size = ${updated.maxLessonSize}
         | where id = $user
    """.stripMargin.update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .log { id => s"Successfully updated user settings id=$id" }
      .map { id => Id[User](id.toString) }
  }

  override def getProfile(id: Id[User]): OptionT[F, Profile] = OptionT {
    sql"""
         | select id, username, full_name, birth_date, bio, picture,
         | (select count(*) from user_kanji uk where uk.user_id = u.id and uk.unlocked = true) as unlocked,
         | (select count(*) from kanji) as unlocked_of, created_at
         | from users as u
         | where id = ${id.toInt}
    """.stripMargin
      .query[Profile]
      .option
      .transact(xa)
      .log(profile => s"Fetched profile by id=$id: $profile")
  }

  def putProfile(user: Id[User], updated: Profile): F[Id[User]] = {
    sql"""
         | update users
         | set username = ${updated.username}, full_name = ${updated.fullName},
         | birth_date = ${updated.birthDate}, bio = ${updated.bio},
         | picture = ${updated.picture}
         | where id = $user
    """.stripMargin.update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(xa)
      .log { id => s"Successfully updated user profile id=$id" }
      .map { id => Id[User](id.toString) }
  }
}

object UserRepository {

  def create[F[_]: Sync: Clock: Logger](xa: Transactor[F]): Resource[F, UserRepositoryAlgebra[F]] =
    Resource.pure(new UserRepository[F](xa))
}
