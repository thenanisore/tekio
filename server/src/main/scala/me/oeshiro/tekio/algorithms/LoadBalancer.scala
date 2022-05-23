package me.oeshiro.tekio.algorithms

import me.oeshiro.tekio.domain.user.UserItem
import me.oeshiro.tekio.utils.Id

import scala.concurrent.duration.FiniteDuration

trait LoadBalancer[F[_]] {

  /**
    * Returns the length of the maximum prefix of the queue,
    * which is estimated to take no more than a specified amount of time.
    */
  def getEstimated(queue: Seq[Id[UserItem]], maxTime: Option[FiniteDuration]): F[(Int, FiniteDuration)]
}
