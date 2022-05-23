package me.oeshiro.tekio.domain.kanji

import cats.syntax.eq._
import io.circe.Encoder
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.CirceConfig._
import me.oeshiro.tekio.utils.Id._

case class Kanji(
    id: Id[Kanji],
    literal: Char,
    readings: Seq[Reading],
    meanings: Seq[Meaning],
    frequencies: Map[String, Double],
    rankings: Map[String, Int],
    strokeCount: Int,
    grade: GradeLevel,
    JLPT: JLPTLevel,
) {
  def onYomi: Seq[Reading] =
    readings.filter(_.readingType === ReadingType.On)

  def kunYomi: Seq[Reading] =
    readings.filter(_.readingType === ReadingType.Kun)
}

object Kanji {
  implicit val kanjiEnc: Encoder[Kanji] = deriveEncoder
}
