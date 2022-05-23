package me.oeshiro.tekio.domain.user

import enumeratum._

object ItemPriority extends Enum[ItemPriority] with CirceEnum[ItemPriority] {
  case object New  extends ItemPriority
  case object High extends ItemPriority
  case object Mid  extends ItemPriority
  case object Low  extends ItemPriority

  val values = findValues
}

sealed trait ItemPriority extends EnumEntry
