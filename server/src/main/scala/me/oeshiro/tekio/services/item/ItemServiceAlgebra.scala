package me.oeshiro.tekio.services.item

import java.time.Instant

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.utils.Id

import scala.concurrent.duration.FiniteDuration

trait ItemServiceAlgebra[F[_]] {

  /**
    * Uploads the list of answers and reschedules user items.
    */
  def finishReviewSession(user: Id[User], answers: Seq[NewUserAnswer]): F[Seq[FullUserKanji]]

  /**
    * Unlocks the learned kanji for the user.
    */
  def finishStudySession(user: Id[User], kanji: Set[Id[Kanji]], timeSpent: FiniteDuration): F[Seq[FullUserKanji]]

  /**
    * Returns the user's answers from the specified time interval.
    */
  def getUserAnswers(user: Id[User], from: Instant, to: Option[Instant] = None,
                     count: Option[Int] = None, offset: Option[Int] = None): F[Seq[UserAnswer]]

  /**
    * Creates initial user kanji for each kanji in a database for a newly created user.
    */
  def createUserKanji(userId: Id[User]): F[Unit]

  /**
    * Returns a list of `count` full kanji starting from the offset
    * with the complete user data about them.
    */
  def getUserKanji(user: Id[User], kanji: Set[Id[Kanji]] = Set.empty, offset: Option[Int] = None,
                   count: Option[Int] = None, unlockedOption: Option[Boolean] = None): F[Seq[FullUserKanji]]

  /**
    * Returns a full kanji by its literal with the complete user data about it.
    */
  def getUserKanji(user: Id[User], literal: String): F[FullUserKanji]


  type ReviewQueue = Seq[(Id[UserItem], Id[Kanji])]

  /**
    * Returns the current review queue of the user.
    */
  def getReviewQueue(user: Id[User], time: Option[FiniteDuration], maxSize: Option[Int]): F[ReviewQueue]

  /**
    * Returns the current lesson queue of the user.
    */
  def getLessonQueue(user: Id[User], time: Option[FiniteDuration], maxLessonsLeft: Int, settings: Settings): F[Seq[Id[Kanji]]]

  /**
    * Unlocks the selected kanji regardless of the current lesson queue
    * and schedules them.
    */
  def unlockKanji(user: Id[User], kanji: Set[Id[Kanji]]): F[Seq[FullUserKanji]]

  /**
    * Applies a new recommendation order based on the given user settings.
    */
  def setKanjiOrder(user: Id[User], settings: Settings): F[Unit]

  /**
    * Returns the amount of time the user spent today on Tekio + number of unlocked kanji today.
    */
  def spentFrom(user: Id[User], from: Instant): F[(FiniteDuration, Int)]
}
