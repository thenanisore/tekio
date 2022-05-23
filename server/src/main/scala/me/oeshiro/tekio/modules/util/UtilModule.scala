package me.oeshiro.tekio.modules.util

import cats.effect.Effect
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.semigroupk._
import io.circe.generic.extras.auto._
import io.circe.syntax._
import me.oeshiro.tekio.modules.util.domain.{EchoRequest, GetInfoResponse}
import me.oeshiro.tekio.services.util.UtilServiceAlgebra
import me.oeshiro.tekio.utils.JsonCodecs._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

/**
  * The utility module that contains a number of utility methods.
  */
class UtilModule[F[_]: Effect](utilService: UtilServiceAlgebra[F])
  extends Http4sDsl[F] {

  /**
    * Path: <b>GET /util/info</b>
    * <br/>
    * Returns the system information about the application.
    * Could be used for debugging purposes.
    */
  private[util] def getInfoHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "info" =>
        for {
          info <- utilService.getAppInfo
          resp <- Ok(
            GetInfoResponse(
              appVersion = info.appVersion,
              appName = info.appName,
              scalaVersion = info.scalaVersion,
              sbtVersion = info.sbtVersion
            ).asJson
          )
        } yield resp
    }

  /**
    * Path: <b>POST /util/echo</b>
    * <br/>
    * <br/>
    * Returns back whatever was sent to the method.
    * Could be used for debugging purposes.
    */
  private[util] def echoHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "echo" =>
        for {
          decodedReq <- req.as[EchoRequest]
          resp       <- Ok(decodedReq.asJson)
        } yield resp
    }

  /** All of the utility module handlers combined. */
  def handlers: HttpRoutes[F] =
    getInfoHandler <+> echoHandler
}

object UtilModule {
  def apply[F[_]: Effect](utilService: UtilServiceAlgebra[F]): HttpRoutes[F] = {
    val module = new UtilModule[F](utilService)
    // prefixes all of the routes with `/util/*`
    Router("/util" -> module.handlers)
  }
}
