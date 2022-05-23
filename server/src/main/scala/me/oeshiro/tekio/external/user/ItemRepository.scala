package me.oeshiro.tekio.external.user

import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit

import cats.data.NonEmptyList
import cats.effect.{Clock, Resource, Sync}
import cats.instances.list._
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import doobie._
import doobie.implicits._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.doobie.implicits._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.external.DTOConverters
import me.oeshiro.tekio.utils.ClockUtils._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.LoggableEffectOps._

import scala.concurrent.duration._

class ItemRepository[F[_]: Logger: Clock](xa: Transactor[F])(implicit F: Sync[F]) extends ItemRepositoryAlgebra[F] {

  implicit val uuidMeta: Meta[UUID] = Meta[String].imap(UUID.fromString)(_.toString)

  implicit val reviewTypeMeta: Meta[ReviewType] =
    Meta[String].imap(ReviewType.withNameInsensitive)(_.entryName.toUpperCase)

  implicit val itemPriorityMeta: Meta[ItemPriority] =
    Meta[String].imap(ItemPriority.withNameInsensitive)(_.entryName.toUpperCase)

  val durationMsMeta: Meta[FiniteDuration] =
    Meta[Int].imap(ms => FiniteDuration(ms, TimeUnit.MILLISECONDS))(_.toMillis.toInt)

  val durationSecondsMeta: Meta[FiniteDuration] =
    Meta[Int].imap(s => FiniteDuration(s, TimeUnit.SECONDS))(_.toSeconds.toInt)

  case class UserKanjiRow(
      id: Id[UserKanji],
      userId: Id[User],
      kanjiId: Id[Kanji],
      unlocked: Boolean,
      unlockedAt: Option[Instant],
      lessonTime: Option[Int],
      rec: Option[Double]
  )

  override def getUserKanji(
      user: Id[User],
      kanji: Set[Id[Kanji]] = Set.empty,
      unlockedOption: Option[Boolean] = None
  ): F[Seq[UserKanji]] = {
    val query             = fr"select id, user_id, kanji_id, unlocked, unlocked_at, lesson_time, rec from user_kanji uk"
    val kanjiList         = kanji.toList
    val idCondition       = kanjiList.headOption.map(h => Fragments.in(fr"uk.kanji_id", NonEmptyList(h, kanjiList.tail)))
    val unlockedCondition = unlockedOption.map(o => fr"unlocked = $o")
    val fullQuery         = query ++ Fragments.whereAndOpt(fr"uk.user_id = $user".some, idCondition, unlockedCondition)
    fullQuery
      .query[UserKanjiRow]
      .to[Seq]
      .transact(xa)
      .logMessage(s"Fetched kanji for user=$user, kanji=${kanji.mkString("(", ", ", ")")}")
      .map(rows => rows.map(row =>
        UserKanji(row.id, row.userId, row.kanjiId, row.unlocked, row.unlockedAt, row.lessonTime.map(_.millis), row.rec)
      ))
  }

  def getUserKanjiById(ids: Seq[Id[UserKanji]]): F[Seq[UserKanji]] =
    ids.toList match {
      case h :: t =>
        val fullQuery =
          fr"""
              | select id, user_id, kanji_id, unlocked, unlocked_at, lesson_time, rec
              | from user_kanji
              | where """.stripMargin ++ Fragments.in(fr"id", NonEmptyList(h, t))
        fullQuery
          .query[UserKanjiRow]
          .to[Seq]
          .transact(xa)
          .logMessage(s"Fetched kanji for ids=${ids.mkString("(", ", ", ")")}")
          .map(rows => rows.map(row =>
            UserKanji(row.id, row.userId, row.kanjiId, row.unlocked, row.unlockedAt, row.lessonTime.map(_.millis), row.rec)
          ))
      case _ =>
        Logger[F].info("No user kanji were fetched by ids, returning Nil") *> Seq.empty[UserKanji].pure[F]
    }

  override def putUserAnswers(answers: Seq[NewUserAnswer], sessionId: FUUID): F[Seq[Id[UserAnswer]]] = {
    implicit val durationMeta = durationMsMeta
    val query                 = s"""
      | insert into user_answers
      | (user_item_id, answered_at, time_taken, is_correct, session_id, answer)
      | values (?, ?, ?, ?, '$sessionId', ?)""".stripMargin
    Update[NewUserAnswer](query)
      .updateManyWithGeneratedKeys[Int]("id")(answers.toList)
      .transact(xa)
      .compile
      .toList
      .log(ids => s"Successfully inserted user items, ids=$ids, answers=$answers")
      .map(_.map(itemId => Id[UserAnswer](itemId.toString)))
  }

  case class NewUserItemInsertRow(
      userKanjiId: Id[UserKanji],
      reviewType: ReviewType,
      interval: FiniteDuration,
      nextReview: Instant
  )

  override def putUserItems(userItems: Seq[NewUserItem]): F[Seq[Id[UserItem]]] = Clock[F].instant.flatMap { now =>
    implicit val durationMeta = durationSecondsMeta
    val batch = userItems.map { i =>
      NewUserItemInsertRow(i.userKanjiId, i.reviewType, i.initialInterval, now.plusSeconds(i.initialInterval.toSeconds))
    }.toList
    val query =
      "insert into user_items (user_kanji_id, review_type, interval_s, next_review) values (?, ?::review_type, ?, ?)"
    Update[NewUserItemInsertRow](query)
      .updateManyWithGeneratedKeys[Int]("id")(batch)
      .transact(xa)
      .compile
      .toList
      .log(ids => s"Successfully inserted user items, ids=$ids")
      .map(_.map(itemId => Id[UserItem](itemId.toString)))
  }

  override def createUserKanji(userId: Id[User]): F[Unit] =
    sql"""
         | insert into user_kanji (user_id, kanji_id, rec)
         | select $userId as user_id, k.id, null
         | from kanji as k;
    """.stripMargin.update.run
      .transact(xa)
      .log(n => s"Successfully inserted $n user kanji")
      .void

  override def getUserAnswers(
      userId: Id[User],
      from: Instant,
      to: Instant,
      count: Option[Int],
      offset: Option[Int]
  ): F[Seq[UserAnswer]] = {
    implicit val durationMeta = durationMsMeta
    sql"""
         | select ua.id, ua.user_item_id, ua.answered_at, ua.time_taken, ua.is_correct, ua.session_id, ua.answer
         | from user_answers as ua
         | inner join user_items as ui on ua.user_item_id = ui.id
         | inner join user_kanji as uk on ui.user_kanji_id = uk.id
         | and uk.user_id = $userId and ua.created_at between $from and $to
         | order by ua.created_at
         | limit $count offset $offset
    """.stripMargin
      .query[UserAnswer]
      .to[Seq]
      .transact(xa)
  }

  case class UserItemUpdateRow(
      interval: Int, // s
      nextReview: Instant,
      finished: Boolean,
      frozen: Boolean,
      priority: ItemPriority,
      answers: Int,
      wins: Int,
      fails: Int,
      averageTime: Int, // ms
      id: Id[UserItem]
  )

  override def updateUserItems(userItems: Seq[UserItem]): F[Unit] = {
    val batch = userItems
      .map(
        i =>
          UserItemUpdateRow(
            i.interval.toSeconds.toInt, // s
            i.nextReview,
            i.finished,
            i.frozen,
            i.priority,
            i.answers,
            i.wins,
            i.fails,
            i.averageTime, // ms
            i.id
          )
      )
      .toList
    val query =
      """
       | update user_items ui
       | set interval_s = ?, next_review = ?, finished = ?, frozen = ?, priority = ?::item_priority,
       | answers = ?, wins = ?, fails = ?, average_time = ?
       | where id = ?
      """.stripMargin
    Update[UserItemUpdateRow](query)
      .updateManyWithGeneratedKeys[Int]("id")(batch)
      .transact(xa)
      .compile
      .toList
      .log(ids => s"Successfully updated user items, ids=$ids")
      .map(_.map(itemId => Id[UserItem](itemId.toString)))
  }

  override def getUserItems(itemIds: Seq[Id[UserItem]]): F[Seq[UserItem]] = {
    implicit val durationMeta = durationSecondsMeta
    itemIds.toList match {
      case h :: t =>
        val fullQuery =
          fr"""
              | select ui.id, ui.user_kanji_id, uk.kanji_id, ui.review_type, ui.interval_s, ui.next_review,
              | ui.finished, ui.frozen, ui.priority, ui.answers, ui.wins, ui.fails, ui.average_time
              | from user_items ui
              | inner join user_kanji uk on uk.id = ui.user_kanji_id
              | where """.stripMargin ++ Fragments.in(fr"ui.id", NonEmptyList(h, t))
        fullQuery
          .query[UserItemFlat]
          .to[Seq]
          .transact(xa)
          .map(_.map(DTOConverters.toUserItem))
          .logMessage(s"Fetched items by ids=${itemIds.mkString("(", ", ", ")")}")
      case _ =>
        Logger[F].info("No items were fetched, returning Nil") *> Seq.empty[UserItem].pure[F]
    }
  }

  override def getUserItemsByKanji(userKanjiId: Seq[Id[UserKanji]]): F[Seq[UserItem]] = {
    implicit val durationMeta = durationSecondsMeta
    userKanjiId.toList match {
      case h :: t =>
        val fullQuery =
          fr"""
              | select ui.id, ui.user_kanji_id, uk.kanji_id, ui.review_type, ui.interval_s, ui.next_review,
              | ui.finished, ui.frozen, ui.priority, ui.answers, ui.wins, ui.fails, ui.average_time
              | from user_items ui
              | inner join user_kanji uk on uk.id = ui.user_kanji_id
              | where """.stripMargin ++ Fragments.in(fr"ui.user_kanji_id", NonEmptyList(h, t))
        fullQuery
          .query[UserItemFlat]
          .to[Seq]
          .transact(xa)
          .map(_.map(DTOConverters.toUserItem))
          .logMessage(s"Fetched all items for user kanji ids=${userKanjiId.mkString("(", ", ", ")")}")
      case _ =>
        Logger[F].info("No items were fetched, returning Nil") *> Seq.empty[UserItem].pure[F]
    }
  }

  override def getReviewQueue(userId: Id[User], count: Option[Int]): F[Seq[(Id[UserItem], Id[Kanji])]] =
    sql"""
         | select ui.id, uk.kanji_id
         | from user_items as ui
         | inner join user_kanji as uk on ui.user_kanji_id = uk.id
         | and uk.user_id = $userId and ui.finished = false and ui.frozen = false and ui.next_review < now()
         | order by ui.next_review asc, ui.priority desc
         | limit $count
    """.stripMargin
      .query[(Id[UserItem], Id[Kanji])]
      .to[Seq]
      .transact(xa)
      .log(queue => s"Fetched review queue of size ${queue.length}")

  override def getLessonQueue(userId: Id[User], count: Option[Int]): F[Seq[Id[Kanji]]] =
    sql"""
         | select kanji_id
         | from user_kanji
         | where user_id = $userId and unlocked = false
         | order by rec desc
         | limit $count
    """.stripMargin
      .query[Id[Kanji]]
      .to[Seq]
      .transact(xa)
      .log(queue => s"Fetched lesson queue of size ${queue.length}")

  case class UpdateUserKanjiRow(unlocked: Boolean, rec: Option[Double], unlockedAt: Option[Instant], id: Id[UserKanji])

  override def updateUserKanji(userKanji: Set[UserKanji]): F[Unit] = {
    val batch: List[UpdateUserKanjiRow] = userKanji.toList.map(
      uk =>
        UpdateUserKanjiRow(
          unlocked = uk.isUnlocked,
          rec = uk.recommendIndex,
          unlockedAt = uk.unlockedAt,
          id = uk.id
        )
    )
    val query =
      """
        | update user_kanji
        | set unlocked = ?, rec = ?, unlocked_at = ?
        | where id = ?
      """.stripMargin
    Update[UpdateUserKanjiRow](query)
      .updateManyWithGeneratedKeys[Int]("id")(batch)
      .transact(xa)
      .compile
      .toList
      .log(ids => s"Successfully updated ${ids.length} user kanji")
      .void
  }

  override def unlockUserKanji(userKanji: Set[Id[UserKanji]], lessonTimeMs: Int): F[Unit] = Clock[F].instant.flatMap { now =>
    val batch: List[(Instant, Int, Id[UserKanji])] = userKanji.toList.map((now, lessonTimeMs, _))
    val query =
      """
        | update user_kanji
        | set unlocked = true, rec = null, unlocked_at = ?, lesson_time = ?
        | where id = ?
      """.stripMargin
    Update[(Instant, Int, Id[UserKanji])](query)
      .updateManyWithGeneratedKeys[Int]("id")(batch)
      .transact(xa)
      .compile
      .toList
      .log(ids => s"Successfully unlocked user kanji, ids=${ids.mkString("(", ", ", ")")}, lesson time $lessonTimeMs ms each")
      .void
  }

  override def getItemOwners(userItems: Set[Id[UserItem]]): F[Seq[Id[User]]] =
    userItems.toList match {
      case h :: t =>
        val fullQuery =
          fr"""
              | select distinct uk.user_id
              | from user_items as ui
              | inner join user_kanji as uk on ui.user_kanji_id = uk.id
              | and """.stripMargin ++ Fragments.in(fr"ui.id", NonEmptyList(h, t))
        fullQuery
          .query[Id[User]]
          .to[Seq]
          .transact(xa)
          .log(
            users =>
              s"Fetched owners for items=${userItems.mkString("(", ", ", ")")}, users=${users.mkString("(", ", ", ")")}"
          )
      case _ =>
        Logger[F].info("No item owners were fetched, returning Nil") *> Seq.empty[Id[User]].pure[F]
    }
}

object ItemRepository {

  def create[F[_]: Sync: Clock: Logger](xa: Transactor[F]): Resource[F, ItemRepositoryAlgebra[F]] =
    Resource.pure(new ItemRepository[F](xa))
}
