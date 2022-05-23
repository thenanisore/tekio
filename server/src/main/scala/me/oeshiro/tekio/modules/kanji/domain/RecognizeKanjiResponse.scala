package me.oeshiro.tekio.modules.kanji.domain

import me.oeshiro.tekio.domain.kanji.KanjiShort

case class RecognizeKanjiResponse(scores: Seq[KanjiShort]);