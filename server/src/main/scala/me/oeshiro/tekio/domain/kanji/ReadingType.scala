package me.oeshiro.tekio.domain.kanji

import enumeratum._

object ReadingType extends Enum[ReadingType] with CatsEnum[ReadingType] with CirceEnum[ReadingType] {
  case object On  extends ReadingType
  case object Kun extends ReadingType

  val values = findValues
}

sealed trait ReadingType extends EnumEntry