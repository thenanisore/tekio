package me.oeshiro.tekio.domain.kanji

import enumeratum._

object GradeLevel extends Enum[GradeLevel] with CatsEnum[GradeLevel] with CirceEnum[GradeLevel] {
  case object G1 extends GradeLevel
  case object G2 extends GradeLevel
  case object G3 extends GradeLevel
  case object G4 extends GradeLevel
  case object G5 extends GradeLevel
  case object G6 extends GradeLevel
  case object G8 extends GradeLevel

  val values = findValues
}

sealed trait GradeLevel extends EnumEntry