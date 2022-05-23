package me.oeshiro.tekio.modules.kanji.domain

case class RecognizeKanjiRequest(strokes: Seq[Seq[(Double, Double)]], count: Int)