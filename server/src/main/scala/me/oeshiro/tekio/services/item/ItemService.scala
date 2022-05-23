package me.oeshiro.tekio.services.item

import java.time.Instant

import cats.MonadError
import cats.effect.{Clock, Sync}
import cats.instances.list._
import cats.syntax.applicative._
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import cats.syntax.traverse._
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.algorithms.{AverageLoadBalancer, FixedRecommender, FixedSRSScheduler, LoadBalancer, Recommender, Scheduler}
import me.oeshiro.tekio.domain.item.{InvalidDataState, InvalidUserAnswers}
import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiShort}
import me.oeshiro.tekio.domain.user.{FullUserKanji, _}
import me.oeshiro.tekio.external.user.ItemRepositoryAlgebra
import me.oeshiro.tekio.services.kanji.KanjiServiceAlgebra
import me.oeshiro.tekio.utils.ClockUtils._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

import scala.util.Random
import scala.concurrent.duration._

class ItemService[F[_]: Sync: Clock: Logger](itemRepo: ItemRepositoryAlgebra[F], kanjiService: KanjiServiceAlgebra[F])
                                            (implicit F: MonadError[F, Throwable]) extends ItemServiceAlgebra[F] {

  val fixedScheduler: Scheduler[F]  = new FixedSRSScheduler[F]
  val loadBalancer: LoadBalancer[F] = new AverageLoadBalancer[F](itemRepo)
  val recommender: Recommender[F]   = new FixedRecommender[F](kanjiService)

  private[item] def validateUserAnswers(user: Id[User], answers: Seq[NewUserAnswer]): F[Unit] =
    itemRepo.getItemOwners(answers.map(_.userItemId).toSet).flatMap { owners =>
      owners.toList match {
        case List(x) if x === user => F.unit
        case _                     => F.raiseError(InvalidUserAnswers("can't change another user's data"))
      }
    }

  /**
    * Uploads the list of answers and reschedules user items.
    */
  override def finishReviewSession(user: Id[User], answers: Seq[NewUserAnswer]): F[Seq[FullUserKanji]] =
    for {
      _         <- validateUserAnswers(user, answers)
      uuid      <- FUUID.randomFUUID[F]
      _         <- itemRepo.putUserAnswers(answers.toList, uuid)
      items     <- itemRepo.getUserItems(answers.map(_.userItemId))
      scheduled <- fixedScheduler.schedule(items, answers)
      _         <- itemRepo.updateUserItems(scheduled)
      updated   <- itemRepo.getUserKanjiById(scheduled.map(_.userKanjiId)).map(_.map(_.kanjiId).toSet)
      full      <- getUserKanji(user, updated)
    } yield full

  private[item] def constructUserItems(userKanji: Seq[UserKanji]): F[Seq[NewUserItem]] = {
    val types     = ReviewType.values
    val userItems = userKanji.map(_.id).flatMap(uk => types.map(t => NewUserItem(uk, t)))
    userItems.pure[F]
  }

  /**
    * Updates user preferences in the repository.
    */
  override def finishStudySession(user: Id[User], kanji: Set[Id[Kanji]], timeSpent: FiniteDuration): F[Seq[FullUserKanji]] =
    for {
      userKanji <- itemRepo.getUserKanji(user, kanji)
      userItems <- constructUserItems(userKanji)
      scheduled <- fixedScheduler.scheduleInitial(userItems)
      _         <- itemRepo.unlockUserKanji(userKanji.map(_.id).toSet, timeSpent.toMillis.toInt / kanji.size)
      _         <- itemRepo.putUserItems(scheduled)
      updated   <- getUserKanji(user, kanji)
    } yield updated

  /**
    * Returns the user's answers from the specified time interval.
    */
  override def getUserAnswers(user: Id[User], from: Instant, to: Option[Instant] = None,
                              count: Option[Int] = None, offset: Option[Int] = None): F[Seq[UserAnswer]] =
    for {
      now     <- Clock[F].instant
      answers <- itemRepo.getUserAnswers(user, from, to.getOrElse(now), count, offset)
    } yield answers

  private[item] def constructFullUserKanji(userKanji: Seq[UserKanji],
                                           kanji: Seq[Kanji], items: Seq[UserItem]): Seq[FullUserKanji] = {
    val itemMap = items.groupBy(_.userKanjiId)
    userKanji
      .sortBy(_.kanjiId)
      .zip(kanji.sortBy(_.id))
      .map {
        case (uk, k) =>
          if (uk.kanjiId === k.id) {
            FullUserKanji(
              id = k.id,
              literal = k.literal,
              readings = k.readings,
              meanings = k.meanings,
              strokeCount = k.strokeCount,
              frequencies = k.frequencies,
              rankings = k.rankings,
              grade = k.grade,
              JLPT = k.JLPT,
              userKanjiId = uk.id,
              isUnlocked = uk.isUnlocked,
              unlockedAt = uk.unlockedAt,
              lessonTime = uk.lessonTime,
              items = itemMap.getOrElse(uk.id, Seq.empty)
            )
          } else throw InvalidDataState("cannot construct full user kanji")
      }
  }

  override def getUserKanji(user: Id[User], kanji: Set[Id[Kanji]] = Set.empty, offset: Option[Int] = None,
                            count: Option[Int] = None, unlockedOption: Option[Boolean] = None): F[Seq[FullUserKanji]] =
    for {
      userKanji <- itemRepo.getUserKanji(user, kanji, unlockedOption)
      kanji     <- kanjiService.getKanjiList(userKanji.map(_.kanjiId), None, None)
      items     <- itemRepo.getUserItemsByKanji(userKanji.map(_.id))
      full = constructFullUserKanji(userKanji, kanji, items)
      withQuiz <- withQuizData(full)
      _        <- Logger[F].info(s"Got ${withQuiz.length} user kanji for user=${user}")
    } yield withQuiz

  private def withQuizData(full: Seq[FullUserKanji]): F[Seq[FullUserKanji]] =
    for {
      all     <- kanjiService.getKanjiShortList(None, None)
      updated <- full.toList.traverse(withQuizData(all.toArray))
    } yield updated

  private def withQuizData(all: Array[KanjiShort])(full: FullUserKanji): F[FullUserKanji] =
    Sync[F].delay {
      // TODO: find similar kanji for quiz
      val rnd          = new Random()
      val others       = Array.fill(3)(all(rnd.nextInt(all.length))).toList.map(_.literal)
      val indexCorrect = rnd.nextInt(4)
      val choices      = others.take(indexCorrect) ::: (full.literal.toString :: others.drop(indexCorrect))
      val updatedItems = full.items.map {
        case item if item.reviewType === ReviewType.Quiz =>
          val question = full.meanings.take(2).map(_.meaning).mkString(", ")
          item.copy(quizData = QuizData(question = question, correct = full.literal.toString, choices = choices).some)
        case item => item
      }
      full.copy(items = updatedItems)
    }

  override def createUserKanji(userId: Id[User]): F[Unit] =
    itemRepo.createUserKanji(userId)

  override def getUserKanji(user: Id[User], literal: String): F[FullUserKanji] =
    for {
      kanji     <- kanjiService.findKanjiByLiteral(literal)
      userKanji <- getUserKanji(user, Set(kanji.id), None, None)
    } yield userKanji.head

  /**
    * Returns the current review queue of the user.
    */
  override def getReviewQueue(user: Id[User], time: Option[FiniteDuration], maxSize: Option[Int]): F[ReviewQueue] = {
    for {
      reviews <- itemRepo.getReviewQueue(user, maxSize)
      (estimated, duration) <- loadBalancer.getEstimated(reviews.map(_._1), time)
      count = maxSize.map(size => size min estimated).getOrElse(estimated)
      _ <- Logger[F].info(s"BALANCING: Estimated review queue of length $count, total $duration")
    } yield reviews.take(count)
  }

  /**
    * Returns the current lesson queue of the user.
    */
  override def getLessonQueue(user: Id[User], time: Option[FiniteDuration], maxLessonsLeft: Int, settings: Settings): F[Seq[Id[Kanji]]] = {
    val maxTimePerLesson = settings.minutesPerDay * 60.0 * 1000.0 / settings.maxLessonSize // ms
    // (time_left / max_lesson_size) or max_left
    val maxLessonsOnly = time.map(t => (t.toMillis / maxTimePerLesson).toInt min maxLessonsLeft) orElse maxLessonsLeft.some
    for {
      queue <- itemRepo.getLessonQueue(user, maxLessonsOnly)
      _     <- Logger[F].info(s"BALANCING: Estimated lesson queue of length $maxLessonsOnly " +
                              s"(actual ${queue.length}) based on given time ${time.map(_.toSeconds)}s")
    } yield queue
 }

  /**
    * Unlocks the selected kanji regardless of the current lesson queue
    * and schedules them.
    */
  override def unlockKanji(user: Id[User], kanji: Set[Id[Kanji]]): F[Seq[FullUserKanji]] =
    // the logic's equivalent to finishing a study session in an instant
    finishStudySession(user, kanji, 0.millis)

  override def setKanjiOrder(user: Id[User], settings: Settings): F[Unit] =
    for {
      locked  <- itemRepo.getUserKanji(user, unlockedOption = false.some)
      updated <- recommender.recalculateWeights(settings, locked)
      _       <- itemRepo.updateUserKanji(updated.toSet)
      _       <- Logger[F].info(s"Set kanji order to ${settings.preferredOrder} for user id=$user")
    } yield ()

  override def spentFrom(user: Id[User], from: Instant): F[(FiniteDuration, Int)] = {
    for {
      startOfDay <- Clock[F].startOfDay
      todayAnswers <- getUserAnswers(user, from = startOfDay)
      spentReviews = todayAnswers.map(_.timeTaken).foldLeft(0.millis)(_ + _)
      unlocked <- getUserKanji(user, unlockedOption = true.some)
      unlockedToday = unlocked.filter(uk => uk.unlockedAt.exists(_.isAfter(from)))
      spentLessons = unlockedToday.flatMap(_.lessonTime).fold(0.millis)(_ + _)
      _ <- Logger[F].info(s"Spent from $from: ${spentReviews.toSeconds}s " +
                          s"on reviews, ${spentLessons.toSeconds}s on lessons, " +
                          s"unlocked today: ${unlockedToday.length}")
    } yield (spentReviews + spentLessons, unlockedToday.length)
  }
}
