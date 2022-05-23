package me.oeshiro.tekio.scripts

import io.circe.parser._
import me.oeshiro.tekio.domain.kanji.JLPTLevel

import scala.io.Source

object JLPTLists {

  val jlptSource = "static/jlpt.json"

  lazy val JLPTMap: Map[String, JLPTLevel] = {
    parse(Source.fromResource(jlptSource).mkString)
      .map { json =>
        json.hcursor
          .as[Map[String, List[String]]]
          .map(m => m.flatMap { case (k, v) => v.map(_ -> JLPTLevel.withNameInsensitive(k)) })
          .getOrElse(throw new Exception("can't decode json"))
      }
      .getOrElse(throw new Exception("can't parse json"))
  }.withDefaultValue(JLPTLevel.N1)
}
