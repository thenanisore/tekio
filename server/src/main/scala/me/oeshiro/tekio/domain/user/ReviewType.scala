package me.oeshiro.tekio.domain.user

import enumeratum._

object ReviewType extends Enum[ReviewType] with CatsEnum[ReviewType] with CirceEnum[ReviewType] {
  case object Meaning extends ReviewType
  case object Reading extends ReviewType
  case object Quiz    extends ReviewType
//  case object Writing extends ReviewType // TODO: implement

  val values = findValues
}

sealed trait ReviewType extends EnumEntry
