package me.oeshiro.tekio.domain.kanji

import enumeratum._

object JLPTLevel extends Enum[JLPTLevel] with CatsEnum[JLPTLevel] with CirceEnum[JLPTLevel] {
  case object N1 extends JLPTLevel
  case object N2 extends JLPTLevel
  case object N3 extends JLPTLevel
  case object N4 extends JLPTLevel
  case object N5 extends JLPTLevel

  val values = findValues
}

sealed trait JLPTLevel extends EnumEntry