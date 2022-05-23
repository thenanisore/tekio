package me.oeshiro.tekio.modules.user.domain

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

case class PostLessonsRequest(
    unlocked: Seq[Id[Kanji]],
    spent: Long
)
