package me.oeshiro.tekio.domain.user

import java.time.Instant

import io.circe.Encoder
import io.circe.generic.semiauto._
import me.oeshiro.tekio.domain.kanji.{GradeLevel, JLPTLevel, Kanji, Meaning, Reading}
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

import scala.concurrent.duration.FiniteDuration

case class FullUserKanji(
    id: Id[Kanji],
    literal: Char,
    readings: Seq[Reading],
    meanings: Seq[Meaning],
    strokeCount: Int,
    frequencies: Map[String, Double],
    rankings: Map[String, Int],
    grade: GradeLevel,
    JLPT: JLPTLevel,
    userKanjiId: Id[UserKanji],
    isUnlocked: Boolean,
    unlockedAt: Option[Instant],
    lessonTime: Option[FiniteDuration],
    items: Seq[UserItem],
    full: Boolean = true
)

object FullUserKanji {

  implicit val durationLongEncoder: Encoder[FiniteDuration] = Encoder.encodeLong.contramap(_.toMillis)

  implicit val fullUserKanjiEncoder: Encoder[FullUserKanji] = deriveEncoder
}