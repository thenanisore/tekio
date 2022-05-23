package me.oeshiro.tekio.external.user

import java.time.Instant

import io.chrisdavenport.fuuid.FUUID
import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.utils.Id

trait ItemRepositoryAlgebra[F[_]] {

  def getUserAnswers(
      userId: Id[User],
      from: Instant,
      to: Instant,
      count: Option[Int],
      offset: Option[Int]
  ): F[Seq[UserAnswer]]

  /**
    * Puts user answers into a repository and assigns them the given session ID.
    */
  def putUserAnswers(answers: Seq[NewUserAnswer], sessionId: FUUID): F[Seq[Id[UserAnswer]]]

  /**
    * Puts new user items for newly unlocked kanji.
    */
  def putUserItems(userItems: Seq[NewUserItem]): F[Seq[Id[UserItem]]]

  def updateUserItems(userItems: Seq[UserItem]): F[Unit]

  def getUserItems(itemIdList: Seq[Id[UserItem]]): F[Seq[UserItem]]

  def getUserItemsByKanji(userKanji: Seq[Id[UserKanji]]): F[Seq[UserItem]]

  def getItemOwners(userItems: Set[Id[UserItem]]): F[Seq[Id[User]]]

  /**
    * Returns user kanji for a user corresponding to the specified kanji ids.
    * Returns all user kanji for the user if the ids are empty.
    *
    * If unlockedOption is set, returns only the items satisfying the condition `unlocked = lockedOption`.
    */
  def getUserKanji(user: Id[User], kanji: Set[Id[Kanji]] = Set.empty,
                   unlockedOption: Option[Boolean] = None): F[Seq[UserKanji]]

  def updateUserKanji(userKanji: Set[UserKanji]): F[Unit]

  def unlockUserKanji(userKanji: Set[Id[UserKanji]], lessonTimeMs: Int): F[Unit]

  def getUserKanjiById(ids: Seq[Id[UserKanji]]): F[Seq[UserKanji]]

  /**
    * Creates initial user kanji for each kanji in a database for a newly created user.
    */
  def createUserKanji(userId: Id[User]): F[Unit]

  def getReviewQueue(userId: Id[User], count: Option[Int]): F[Seq[(Id[UserItem], Id[Kanji])]]

  def getLessonQueue(userId: Id[User], count: Option[Int]): F[Seq[Id[Kanji]]]
}
