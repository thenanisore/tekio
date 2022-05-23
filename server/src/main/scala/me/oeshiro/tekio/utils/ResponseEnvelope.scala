package me.oeshiro.tekio.utils

import cats.data.Kleisli
import cats.effect.Sync
import cats.instances.string._
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.extras.auto._
import me.oeshiro.tekio.utils.JsonCodecs._
import org.http4s.headers.{Connection, `Content-Length`}
import org.http4s.{Headers, MediaType, Request, Response, Status}
import org.http4s.syntax.string._

import scala.util.control.NonFatal

/**
  * Middleware for enveloping plain text responses in a json.
  */
object ResponseEnvelope {

  case class TextResponseEnvelope(info: String)

  def apply[F[_]: Sync: Logger](http: Kleisli[F, Request[F], Response[F]]): Kleisli[F, Request[F], Response[F]] =
    Kleisli[F, Request[F], Response[F]] { req =>
      http(req)
        .flatMap { response =>
          response.contentType.map(_.mediaType) match {
            case Some(MediaType.text.plain) | None =>
              response.bodyAsText.compile.foldMonoid.map { b =>
                val msg = b.some.map(_.trim).filterNot(_.isEmpty).getOrElse(response.status.reason)
                response.withEntity(TextResponseEnvelope(msg))
              }
            case _ => response.pure[F]
          }
        }
        .handleErrorWith {
          // in case of a 500 response - form a json with the information about the internal error
          case NonFatal(e) =>
            val resp = Response[F](
              Status.InternalServerError,
              req.httpVersion,
              Headers(
                Connection("close".ci),
                `Content-Length`.zero
              )
            ).withEntity(TextResponseEnvelope(e.getMessage))
            Logger[F].error(e)(e.getMessage) *> resp.pure[F]
        }
    }
}
