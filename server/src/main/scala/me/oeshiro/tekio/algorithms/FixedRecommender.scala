package me.oeshiro.tekio.algorithms

import cats.Monad
import cats.syntax.functor._
import cats.syntax.option._
import me.oeshiro.tekio.domain.user.{Settings, UserKanji}
import me.oeshiro.tekio.services.kanji.KanjiServiceAlgebra

class FixedRecommender[F[_]: Monad](kanjiService: KanjiServiceAlgebra[F]) extends Recommender[F] {

  /**
    * Recalculates the recommendation weight of user kanji based on user settings.
    */
  override def recalculateWeights(settings: Settings, userKanji: Seq[UserKanji]): F[Seq[UserKanji]] =
    for {
      kanji <- kanjiService.getKanjiList(userKanji.map(_.kanjiId), None, None)
    } yield {
      val kanjiMap = kanji.groupBy(_.id).mapValues(_.head)
      userKanji.map { uk =>
        val corresponding = kanjiMap.getOrElse(uk.kanjiId,
          throw new Exception(s"Cannot recalculate weights: no kanji found for id=${uk.kanjiId}")
        )
        // if there is was no info in the selected dataset, mark this kanji insignificant
        val index = corresponding.frequencies.getOrElse(settings.preferredOrder.entryName, 0.0)
        uk.copy(recommendIndex = index.some)
      }
    }
}
