package me.oeshiro.tekio.domain.user

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.utils.CirceConfig._

final case class Settings(
    minutesPerDay: Int,
    preferredOrder: OrderType,
    adaptiveOrder: Boolean,
    adaptiveScheduling: Boolean,
    batchSize: Int,
    autoReveal: Boolean,
    maxReviewSize: Int,
    maxLessonSize: Int
)

object Settings {
  implicit val settingsEnc: Encoder[Settings] = deriveEncoder
  implicit val settingsDec: Decoder[Settings] = deriveDecoder
}
