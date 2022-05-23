package me.oeshiro.tekio.domain.user

import java.time.Instant

import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.circe._
import io.circe.generic.extras.semiauto._
import io.circe.Encoder
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.CirceConfig._

import scala.concurrent.duration.FiniteDuration

case class UserAnswer(
    id: Id[UserAnswer],
    userItemId: Id[UserItem],
    answeredAt: Instant,
    timeTaken: FiniteDuration,
    isCorrect: Boolean,
    sessionId: FUUID,
    answer: Option[String]
)

object UserAnswer {

  implicit val durationLongEncoder: Encoder[FiniteDuration] = Encoder.encodeLong.contramap(_.toMillis)

  implicit val instantLongEncoder: Encoder[Instant] = Encoder.encodeLong.contramap(_.getEpochSecond)

  implicit val userAnswerEncoder: Encoder[UserAnswer] = deriveEncoder
}
