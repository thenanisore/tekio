package me.oeshiro.tekio

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto._

package object config {

  implicit val defaultConfig: Configuration = Configuration.default.withDefaults

  // config file decoders, derived from case class definitions
  implicit val databaseConfigDec: Decoder[DatabaseConfig] = deriveDecoder
  implicit val serverConfigDec: Decoder[ServerConfig]     = deriveDecoder
  implicit val authConfigDec: Decoder[AuthConfig]         = deriveDecoder
  implicit val corsDec: Decoder[CorsConfig]               = deriveDecoder
  implicit val configDec: Decoder[Config]                 = deriveDecoder
  implicit val userDec: Decoder[UserConfig]               = deriveDecoder
}
