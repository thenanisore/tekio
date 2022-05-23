package me.oeshiro.tekio.domain.kanji

import me.oeshiro.tekio.utils.Id

case class KanjiShort(
    id: Id[Kanji],
    literal: String,
    grade: GradeLevel,
    JLPT: JLPTLevel,
    meaningsShort: String
)
