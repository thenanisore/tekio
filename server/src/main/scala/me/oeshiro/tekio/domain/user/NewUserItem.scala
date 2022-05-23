package me.oeshiro.tekio.domain.user

import me.oeshiro.tekio.utils.Id

import scala.concurrent.duration._

case class NewUserItem(
    userKanjiId: Id[UserKanji],
    reviewType: ReviewType,
    initialInterval: FiniteDuration = 0.seconds
)
