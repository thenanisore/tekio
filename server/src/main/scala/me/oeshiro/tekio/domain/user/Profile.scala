package me.oeshiro.tekio.domain.user

import java.time.LocalDate

import io.circe.Encoder
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.utils.CirceConfig._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._

case class Profile(
    id: Id[User],
    username: String,
    fullName: Option[String] = None,
    birthDate: Option[LocalDate] = None,
    bio: Option[String] = None,
    picture: Option[String] = None,
    unlocked: Int,
    unlockedOf: Int,
    joined: LocalDate
)

object Profile {

  implicit val profileEnc: Encoder[Profile] = deriveEncoder
}
