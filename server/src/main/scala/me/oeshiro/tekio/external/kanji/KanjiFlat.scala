package me.oeshiro.tekio.external.kanji

import me.oeshiro.tekio.domain.kanji.ReadingType

case class KanjiFlat(
    id: Int,
    literal: String,
    strokeCount: Int,
    aozoraFreq: Option[Double],
    newsFreq: Option[Double],
    twitterFreq: Option[Double],
    wikiFreq: Option[Double],
    aozoraRank: Option[Int],
    newsRank: Option[Int],
    twitterRank: Option[Int],
    wikiRank: Option[Int],
    grade: String,
    jlpt: String,
    readingId: Int,
    reading: String,
    readingType: ReadingType,
    meaningId: Int,
    meaning: String
)
