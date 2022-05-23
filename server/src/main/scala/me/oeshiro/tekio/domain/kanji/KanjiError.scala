package me.oeshiro.tekio.domain.kanji

abstract class KanjiError(msg: String) extends Exception(msg)
case class KanjiNotFound(msg: String) extends KanjiError(s"Could not find kanji with $msg")
