package me.oeshiro.tekio.algorithms

import java.time.Instant

import cats.Monad
import cats.effect.Clock
import cats.instances.int._
import cats.syntax.applicative._
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import me.oeshiro.tekio.algorithms.FixedSRSScheduler._
import me.oeshiro.tekio.domain.user.{ItemPriority, NewUserAnswer, NewUserItem, UserItem}
import me.oeshiro.tekio.utils.ClockUtils._
import me.oeshiro.tekio.utils.Id

import scala.concurrent.duration._

/**
  * A fixed SRS scheduler implements a simple SRS algorithms with the sequence of fixed intervals.
  * The intervals are fixed as follows:
  * - 4h
  * - 8h
  * - 23h   (1d - 1h)
  * - 47h   (2d - 1h) -> Guru
  * - 167h  (1w - 1h)
  * - 335h  (2w - 1h) -> Master
  * - 719h  (1m - 1h) -> Enlighten (where 1m == 30d)
  * - 2879h (4m - 1h) -> Buried
  *
  * A buried item is considered finished.
  */
class FixedSRSScheduler[F[_]: Monad: Clock] extends Scheduler[F] {

  private[algorithms] def getPriority(item: UserItem, correct: Boolean): ItemPriority = {
    if (item.answers > 4) {
      // if old enough item
      val winRate = item.wins.toDouble / item.fails
      winRate match {
        case rate if rate < 0.5 => ItemPriority.High // high if more than 2 fails to 1 win
        case rate if rate < 3.0 => ItemPriority.Mid // mid if before 3 wins to 1 fail
        case _ if correct       => ItemPriority.Low // low if 2easy4me
        case _                  => ItemPriority.Mid // mid if easy, but the current one was failed
      }
    } else {
      // if fresh enough item
      if (correct) ItemPriority.Mid else ItemPriority.High
    }
  }

  private[algorithms] def reschedule(item: UserItem, isCorrect: Boolean, now: Instant): UserItem =
    if (isCorrect) {
      // in case of failure - the previous interval wasn't set properly, finding the nearest next one
      val nextOpt = nextInterval.getOrElse(item.interval, intervals.find(_ > item.interval))
      nextOpt match {
        case Some(next) =>
          item.copy(
            interval = next,
            nextReview = now.plusMillis(next.toMillis),
            wins = item.wins + 1,
          )
        case None =>
          item.copy(finished = true)
      }
    } else {
      val prev =
        prevInterval.getOrElse(item.interval, intervals.reverse.find(_ < item.interval).getOrElse(intervals.head))
      item.copy(
        interval = prev,
        nextReview = now.plusMillis(prev.toMillis),
        fails = item.fails + 1,
      )
    }

  private[algorithms] def updateStats(item: UserItem, answers: Seq[NewUserAnswer], correct: Boolean): UserItem = {
    val totalAns = item.answers + answers.length
    val totalTime = (item.averageTime * item.answers) + answers.map(_.timeTaken.toMillis.toInt).sum
    val newAverage: Double = totalTime.toDouble / totalAns
    item.copy(
      averageTime = newAverage.toInt, // ms
      answers = totalAns,
      priority = getPriority(item, correct)
    )
  }

  /**
    * Reschedules the SRS intervals after a review session.
    */
  override def schedule(userItems: Seq[UserItem], answers: Seq[NewUserAnswer]): F[Seq[UserItem]] = {
    // In fixed SRS we consider the review successful if it was answered right first time.
    val answersByUserItem = answers.groupBy(_.userItemId)
    val isCorrect: Id[UserItem] => Boolean = answersByUserItem
      .mapValues(answers => answers.length === 1 && answers.forall(_.isCorrect))

    for {
      now   <- Clock[F].instant
      items <- userItems
        .toStream
        .map(item => reschedule(item, isCorrect(item.id), now))
        .map(item => updateStats(item, answersByUserItem(item.id), isCorrect(item.id)))
        .toList
        .pure[F]
    } yield {
      items
    }
  }

  override def scheduleInitial(userItems: Seq[NewUserItem]): F[Seq[NewUserItem]] =
    userItems
      .map(_.copy(initialInterval = FixedSRSScheduler.intervals.head))
      .pure[F]
}

object FixedSRSScheduler {

  val intervals: List[FiniteDuration] =
    List(62.minutes, 5.hours, 9.hours, 1.day, 2.days, 7.days, 14.days, 30.days, 120.days).map(_ - 1.hour)

  val nextInterval: Map[FiniteDuration, Option[FiniteDuration]] =
    intervals.zip(intervals.tail.map(_.some)).toMap + (intervals.last -> None)

  val prevInterval: Map[FiniteDuration, FiniteDuration] =
    intervals.tail.zip(intervals).toMap + (intervals.head -> intervals.head)
}
