package me.oeshiro.tekio.algorithms

import me.oeshiro.tekio.domain.user.{NewUserAnswer, NewUserItem, UserItem}

trait Scheduler[F[_]] {

  /**
    * Schedules the initial SRS intervals after a study session.
    */
  def scheduleInitial(userItems: Seq[NewUserItem]): F[Seq[NewUserItem]]

  /**
    * Reschedules the SRS intervals after a review session.
    */
  def schedule(userItems: Seq[UserItem], answers: Seq[NewUserAnswer]): F[Seq[UserItem]]
}
