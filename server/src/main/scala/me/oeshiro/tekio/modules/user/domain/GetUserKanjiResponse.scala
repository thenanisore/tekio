package me.oeshiro.tekio.modules.user.domain

import me.oeshiro.tekio.domain.user.FullUserKanji

case class GetUserKanjiResponse(kanji: Seq[FullUserKanji])
