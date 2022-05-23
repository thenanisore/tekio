package me.oeshiro.tekio.utils

import io.circe.generic.extras.Configuration

object CirceConfig {
  implicit val camelCaseConfig: Configuration = Configuration.default
}
