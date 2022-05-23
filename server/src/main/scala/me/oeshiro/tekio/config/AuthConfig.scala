package me.oeshiro.tekio.config

import io.circe.Decoder

case class AuthConfig(jwtExpireSeconds: Long, secret: String)

object AuthConfig {
  implicit val longDecoder: Decoder[Long] = io.circe.Decoder.decodeLong
}
