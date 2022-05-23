package me.oeshiro.tekio.modules.user.domain

import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.domain.user.UserItem

case class GetReviewsResponse(queue: Seq[UserItem], kanji: Seq[Kanji])

