package me.oeshiro.tekio.algorithms

import me.oeshiro.tekio.domain.user.{Settings, UserKanji}

trait Recommender[F[_]] {

  /**
    * Recalculates the recommendation weight of user kanji.
    */
  def recalculateWeights(settings: Settings, userKanji: Seq[UserKanji]): F[Seq[UserKanji]]
}
