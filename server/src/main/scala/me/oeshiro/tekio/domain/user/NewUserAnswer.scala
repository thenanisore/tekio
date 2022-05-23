package me.oeshiro.tekio.domain.user

import java.time.Instant
import java.util.concurrent.TimeUnit

import io.circe.Decoder
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.CirceConfig._
import me.oeshiro.tekio.utils.Id._

import scala.concurrent.duration.FiniteDuration

case class NewUserAnswer(
    userItemId: Id[UserItem],
    answeredAt: Instant,
    timeTaken: FiniteDuration,
    isCorrect: Boolean,
    answer: String
)

object NewUserAnswer {

  implicit val instantLongDecoder: Decoder[Instant] = Decoder.decodeLong.map(Instant.ofEpochSecond)

  implicit val durationLongDecoder: Decoder[FiniteDuration] = Decoder.decodeLong.map(FiniteDuration(_, TimeUnit.MILLISECONDS))

  implicit val newUserAnswerDecoder: Decoder[NewUserAnswer] = deriveDecoder
}
