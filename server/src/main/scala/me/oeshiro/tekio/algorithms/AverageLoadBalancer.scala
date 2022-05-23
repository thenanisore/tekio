package me.oeshiro.tekio.algorithms

import cats.Monad
import cats.syntax.functor._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.domain.user.UserItem
import me.oeshiro.tekio.external.user.ItemRepositoryAlgebra
import me.oeshiro.tekio.utils.Id

import scala.concurrent.duration._

class AverageLoadBalancer[F[_]: Monad: Logger](itemRepo: ItemRepositoryAlgebra[F]) extends LoadBalancer[F] {

  private[algorithms] def estimate(items: Seq[UserItem], maxTime: Option[FiniteDuration]): (Int, FiniteDuration) =
    maxTime match {
      case Some(max) =>
        items.foldLeft(0 -> 0.millis) {
          case ((count, accTime), item) =>
            if (accTime > max) (count, accTime)
            else (count + 1, accTime + item.averageTime.millis)
        }
      case None => (items.length, items.map(_.averageTime).sum.millis)
    }

  /**
    * Returns the length of the maximum prefix of the queue,
    * which is estimated to take no more than a specified number of time.
    */
  def getEstimated(queue: Seq[Id[UserItem]], maxTime: Option[FiniteDuration]): F[(Int, FiniteDuration)] =
    for {
      items <- itemRepo.getUserItems(queue)
      (count, estimatedTime) = estimate(items, maxTime)
    } yield (count, estimatedTime)
}
