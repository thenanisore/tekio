package me.oeshiro.tekio.services.util

import cats.Applicative
import cats.syntax.applicative._
import me.oeshiro.tekio.domain.util.AppInfo
import me.oeshiro.tekio.utils.BuildInfo

class UtilService[F[_]: Applicative] extends UtilServiceAlgebra[F] {
  def getAppInfo: F[AppInfo] = {
    val info = AppInfo(
      appVersion = BuildInfo.version,
      appName = BuildInfo.name,
      scalaVersion = BuildInfo.scalaVersion,
      sbtVersion = BuildInfo.sbtVersion
    )
    info.pure[F]
  }
}
