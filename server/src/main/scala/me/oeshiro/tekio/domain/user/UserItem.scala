package me.oeshiro.tekio.domain.user

import java.time.Instant

import io.circe.Encoder
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.CirceConfig._
import me.oeshiro.tekio.utils.Id._

import scala.concurrent.duration.FiniteDuration

/**
  * A user item is a user-specific pair (Kanji, ReviewType).
  * For example, a kanji `Sun` can have items: `Sun/reading`, `Sun/meaning`, etc.
  *
  * The intervals and user progress are calculated for each item independently.
  */
case class UserItem(
    id: Id[UserItem],
    userKanjiId: Id[UserKanji],
    kanjiId: Id[Kanji],
    reviewType: ReviewType,
    interval: FiniteDuration, // s
    nextReview: Instant,
    finished: Boolean,
    frozen: Boolean,
    priority: ItemPriority,
    answers: Int,
    wins: Int,
    fails: Int,
    averageTime: Int, // ms
    quizData: Option[QuizData] = None
)

case class QuizData(
    question: String,
    correct: String,
    choices: List[String]
)

object UserItem {

  implicit val instantLongEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.getEpochSecond)

  implicit val durationLongEncoder: Encoder[FiniteDuration] = Encoder.encodeLong.contramap(_.toSeconds)

  implicit val quizData: Encoder[QuizData] = deriveEncoder

  implicit val userItemEnd: Encoder[UserItem] = deriveEncoder
}
