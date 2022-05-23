package me.oeshiro.tekio.external.user

import java.time.Instant

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user.{ItemPriority, ReviewType, UserItem, UserKanji}
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

import scala.concurrent.duration.FiniteDuration

/**
  * A user item is a user-specific pair (Kanji, ReviewType).
  * For example, a kanji `Sun` can have items: `Sun/reading`, `Sun/meaning`, etc.
  *
  * The intervals and user progress are calculated for each item independently.
  */
case class UserItemFlat(
    id: Id[UserItem],
    userKanjiId: Id[UserKanji],
    kanjiId: Id[Kanji],
    reviewType: ReviewType,
    interval: FiniteDuration,
    nextReview: Instant,
    finished: Boolean,
    frozen: Boolean,
    priority: ItemPriority,
    answers: Int,
    wins: Int,
    fails: Int,
    averageTime: Int
)
