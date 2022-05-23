package me.oeshiro.tekio.domain.user

import enumeratum._

object OrderType extends Enum[OrderType] with CatsEnum[OrderType] with CirceEnum[OrderType] {
  case object Aozora    extends OrderType
  case object News      extends OrderType
  case object Twitter   extends OrderType
  case object Wikipedia extends OrderType

  val values = findValues
}

sealed trait OrderType extends EnumEntry
