package me.oeshiro.tekio.modules.user.domain

case class PutSettingsRequest(
    minutesPerDay: Option[Int],
    preferredOrder: Option[String]
)
