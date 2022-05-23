package me.oeshiro.tekio.domain.kanji

import io.circe.Encoder
import io.circe.generic.extras.semiauto._
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.CirceConfig._
import me.oeshiro.tekio.utils.Id._

case class Meaning(
    id: Id[Meaning],
    meaning: String
)

object Meaning {
  implicit val meaningEnc: Encoder[Meaning] = deriveEncoder
}
