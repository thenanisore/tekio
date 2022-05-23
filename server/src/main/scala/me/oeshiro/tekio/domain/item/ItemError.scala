package me.oeshiro.tekio.domain.item

abstract class ItemError(msg: String)      extends Exception(msg)
case class InvalidUserAnswers(msg: String) extends ItemError(s"Can't upload answers: $msg")
case class InvalidDataState(msg: String)   extends ItemError(s"Data obtained from the database is invalid: $msg.")
