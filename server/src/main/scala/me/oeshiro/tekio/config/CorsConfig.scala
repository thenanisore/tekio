package me.oeshiro.tekio.config

import cats.Applicative
import cats.syntax.applicative._
import cats.syntax.option._
import io.chrisdavenport.log4cats.Logger
import org.http4s.server.middleware.CORSConfig

import scala.concurrent.duration._

case class CorsConfig(allowedOrigins: List[String], allowedHeaders: List[String])

object CorsConfig {
  def getConfig[F[_]: Applicative](config: CorsConfig): F[CORSConfig] = {
    val allowedOriginSet: String => Boolean =
      if (config.allowedOrigins.contains("*")) _ => true
      else config.allowedOrigins.toSet
    CORSConfig(
      anyOrigin = false,
      allowCredentials = true,
      maxAge = 1.day.toSeconds,
      anyMethod = true,
      allowedOrigins = allowedOriginSet,
      allowedHeaders = config.allowedHeaders.toSet.some,
    ).pure[F]
  }
}
