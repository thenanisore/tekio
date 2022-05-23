package me.oeshiro.tekio.services.util

import me.oeshiro.tekio.domain.util.AppInfo

trait UtilServiceAlgebra[F[_]] {

  /**
    * Returns the info about the current server app build.
    */
  def getAppInfo: F[AppInfo]
}
