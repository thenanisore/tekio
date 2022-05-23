package me.oeshiro.tekio.scripts

import io.circe.parser._
import scala.io.Source

object FrequencyLists {

  lazy val aozoraMap: Map[String, (Int, Double)] = getFrequencyMap("aozora")
  lazy val newsMap: Map[String, (Int, Double)] = getFrequencyMap("news")
  lazy val twitterMap: Map[String, (Int, Double)] = getFrequencyMap("twitter")
  lazy val wikiMap: Map[String, (Int, Double)] = getFrequencyMap("wikipedia")

  type FreqEntry = (String, Int, Double)

  def getFrequencyMap(source: String): Map[String, (Int, Double)] = {
    parse(Source.fromResource(s"static/$source.json").mkString)
      .map { json =>
        json.hcursor
          .as[List[FreqEntry]]
          .map(entries => entries.zipWithIndex.map { case ((literal, _, freq), i) => literal -> (i -> freq) }.toMap)
          .getOrElse(throw new Exception("can't decode json"))
      }
      .getOrElse(throw new Exception("can't parse json"))
  }
}
