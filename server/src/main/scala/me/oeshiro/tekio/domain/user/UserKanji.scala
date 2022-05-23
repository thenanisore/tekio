package me.oeshiro.tekio.domain.user

import java.time.Instant

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

import scala.concurrent.duration.FiniteDuration

/**
  * The user information about a specific kanji.
  *
  * @param recommendIndex the bigger this is, the more likely the character is to be recommended
  */
case class UserKanji(
    id: Id[UserKanji],
    userId: Id[User],
    kanjiId: Id[Kanji],
    isUnlocked: Boolean,
    unlockedAt: Option[Instant],
    lessonTime: Option[FiniteDuration],
    recommendIndex: Option[Double],
    kanji: Option[Kanji] = None,
    items: Seq[UserItem] = Seq.empty
)
