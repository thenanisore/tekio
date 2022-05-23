package me.oeshiro.tekio.scripts

import java.io.{BufferedWriter, File, FileWriter}

import me.oeshiro.tekio.domain.kanji.{GradeLevel, JLPTLevel, ReadingType}

import scala.collection.mutable
import scala.io.Source
import scala.xml.XML

case class KanjiEntry(literal: String,
                      strokeCount: Int,
                      grade: GradeLevel,
                      aozoraFreq: Option[Double],
                      newsFreq: Option[Double],
                      twitterFreq: Option[Double],
                      wikiFreq: Option[Double],
                      aozoraRank: Option[Int],
                      newsRank: Option[Int],
                      twitterRank: Option[Int],
                      wikiRank: Option[Int],
                      jlpt: JLPTLevel)

case class ReadingEntry(kanjiId: Int, reading: String, readingType: ReadingType)

case class MeaningEntry(kanjiId: Int, meaning: String)

object KanjiDicImporter {

  val outputKanji = "./src/main/resources/db/kanjidic_kanji.sql"
  val outputReading = "./src/main/resources/db/kanjidic_readings.sql"
  val outputMeanings = "./src/main/resources/db/kanjidic_meanings.sql"

  val joyoGrades = Set("1", "2", "3", "4", "5", "6", "8")

  val kanjiBuffer = mutable.ListBuffer.empty[KanjiEntry]
  val readingBuffer = mutable.ListBuffer.empty[ReadingEntry]
  val meaningBuffer = mutable.ListBuffer.empty[MeaningEntry]

  println("Loading and parsing XML...")
  XML
    .load(Source.fromResource("static/kanjidic2.xml").reader())
    .child
    .tail
    .filter { k =>
      val gradeOpt = (k \\ "grade").headOption
      gradeOpt.exists(n => joyoGrades(n.text))
    }
    .zipWithIndex
    .foreach {
      case (k, index) =>
        val dbIndex = index + 1

        // kanji attributes
        val literal = (k \\ "literal").text
        val jlpt = (k \\ "misc" \\ "jlpt").text
        val grade = (k \\ "misc" \\ "grade").text
        val strokeCount = (k \\ "misc" \\ "stroke_count").head.text

        kanjiBuffer.append(
          KanjiEntry(
            literal = literal,
            strokeCount = strokeCount.toInt,
            grade = GradeLevel.withNameInsensitive(s"G$grade"),
            aozoraFreq = FrequencyLists.aozoraMap.get(literal).map(_._2),
            newsFreq = FrequencyLists.newsMap.get(literal).map(_._2),
            twitterFreq = FrequencyLists.twitterMap.get(literal).map(_._2),
            wikiFreq = FrequencyLists.wikiMap.get(literal).map(_._2),
            aozoraRank = FrequencyLists.aozoraMap.get(literal).map(_._1),
            newsRank = FrequencyLists.newsMap.get(literal).map(_._1),
            twitterRank = FrequencyLists.twitterMap.get(literal).map(_._1),
            wikiRank = FrequencyLists.wikiMap.get(literal).map(_._1),
            jlpt = JLPTLists.JLPTMap(literal)
          )
        )

        val readings = k \\ "reading_meaning" \\ "reading"
        val on = readings
          .filter(r => r.attribute("r_type").exists(_.head.text == "ja_on"))
          .map(n => ReadingEntry(dbIndex, n.text, ReadingType.On))
        val kun = readings
          .filter(r => r.attribute("r_type").exists(_.head.text == "ja_kun"))
          .map(n => ReadingEntry(dbIndex, n.text, ReadingType.Kun))
        readingBuffer.appendAll(on ++ kun)

        val meanings = (k \\ "reading_meaning" \\ "meaning")
          .filter(m => m.attribute("m_lang").isEmpty)
          .map(n => MeaningEntry(dbIndex, prepareString(n.text)))
        meaningBuffer.appendAll(meanings)
    }
  println("Finished parsing XML")

  println("Generating DDL...")
  val ddlKanji = generateDDLKanji(kanjiBuffer)
  val ddlReadings = generateDDLKanjiReadings(readingBuffer)
  val ddlMeanings = generateDDLKanjiMeanings(meaningBuffer)
  println("Finished generating DDL")

  val bw1 = new BufferedWriter(new FileWriter(new File(outputKanji)))
  try {
    println(s"Writing kanji DDL to $outputKanji...")
    bw1.write(ddlKanji)
  } finally bw1.close()
  println(s"Finished writing kanji DDL")

  val bw2 = new BufferedWriter(new FileWriter(new File(outputReading)))
  try {
    println(s"Writing readings DDL to $outputReading...")
    bw2.write(ddlReadings)
  } finally bw2.close()
  println(s"Finished writing readings DDL")

  val bw3 = new BufferedWriter(new FileWriter(new File(outputMeanings)))
  try {
    println(s"Writing meanings DDL to $outputMeanings...")
    bw3.write(ddlMeanings)
  } finally bw3.close()
  println(s"Finished writing meanings DDL")

  def generateDDLKanji(kanji: Seq[KanjiEntry]): String = {
    val tuples = kanji.map(
      k =>
        s"('${k.literal}', ${k.strokeCount}, '${k.grade.entryName}'::grade_level," +
          s"${k.aozoraFreq.getOrElse("null")}, ${k.newsFreq.getOrElse("null")}, ${k.twitterFreq.getOrElse("null")}, ${k.wikiFreq.getOrElse("null")}," +
          s"${k.aozoraRank.getOrElse("null")}, ${k.newsRank.getOrElse("null")}, ${k.twitterRank.getOrElse("null")}, ${k.wikiRank.getOrElse("null")}," +
          s"'${k.jlpt.entryName}'::jlpt_level)"
    )
    s"insert into kanji (literal, stroke_count, grade, aozora_freq, news_freq, twitter_freq, wiki_freq, aozora_rank, news_rank, twitter_rank, wiki_rank, jlpt)\n" +
      s"values ${tuples.mkString(",\n")};"
  }

  def generateDDLKanjiReadings(readings: Seq[ReadingEntry]): String = {
    val tuples = readings.map(r => s"(${r.kanjiId}, '${r.reading}', '${r.readingType.entryName.toUpperCase}'::reading_type)")
    s"\ninsert into kanji_readings (kanji_id, reading, type)\nvalues ${tuples.mkString(",\n")};"
  }

  def generateDDLKanjiMeanings(meanings: Seq[MeaningEntry]): String = {
    val tuples = meanings.map(m => s"(${m.kanjiId}, '${m.meaning}')")
    s"insert into kanji_meanings (kanji_id, meaning)\nvalues ${tuples.mkString(",\n")};"
  }

  def prepareString(s: String): String =
    s.replaceAll("'", "''")
}
