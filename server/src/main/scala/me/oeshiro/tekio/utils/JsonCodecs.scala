package me.oeshiro.tekio.utils

import cats.effect.Sync
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder}

/** A helper object for auto-derivation of EntityEncoders. */
object JsonCodecs {

  implicit val config: Configuration = CirceConfig.camelCaseConfig

  implicit def jsonEncoder[F[_]: Sync, A <: Product : Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit def jsonDecoder[F[_]: Sync, A <: Product : Decoder]: EntityDecoder[F, A] =
    jsonOf[F, A]
}
