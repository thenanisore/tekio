package me.oeshiro.tekio.modules.kanji

import cats.effect.Effect
import cats.syntax.applicativeError._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.semigroupk._
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.extras.auto._
import io.circe.syntax._
import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiNotFound}
import me.oeshiro.tekio.modules.kanji.domain._
import me.oeshiro.tekio.services.kanji.KanjiServiceAlgebra
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.JsonCodecs._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

/**
  * The Kanji module contains functions for getting non-user-specific information about kanji and vocabulary.
  */
class KanjiModule[F[_]: Effect: Logger](kanjiService: KanjiServiceAlgebra[F]) extends Http4sDsl[F] {

  object OffsetParam extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object CountParam  extends OptionalQueryParamDecoderMatcher[Int]("count")
  object IndexParam  extends QueryParamDecoderMatcher[String]("ids")

  /**
    * Path: <b>GET /kanji?offset=:int:&count=:int:</b>
    * <br/>
    * Returns a list of `count` kanji starting from the offset.
    */
  private[kanji] def kanjiShortListHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root :? OffsetParam(offset) +& CountParam(count) =>
        for {
          kanjiList <- kanjiService.getKanjiShortList(offset, count)
          resp      <- Ok(KanjiShortListResponse(kanjiList).asJson)
        } yield resp
    }

  /**
    * Path: <b>GET /kanji?ids=1,2,3,4&offset=:int:&count=:int:</b>
    * <br/>
    * Returns a list of `count` full kanji starting from the offset.
    */
  private[kanji] def kanjiListHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root :? IndexParam(ids) +& OffsetParam(offset) +& CountParam(count) =>
        val indices = ids.split(',').map(Id[Kanji])
        for {
          kanjiList <- kanjiService.getKanjiList(indices, offset, count)
          resp      <- Ok(KanjiListResponse(kanjiList).asJson)
        } yield resp
    }

  /**
    * Path: <b>GET /kanji/:id</b>
    * <br/>
    * Returns a kanji by id.
    */
  private[kanji] def kanjiHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / id =>
        val response = for {
          kanji <- kanjiService.findKanjiById(Id[Kanji](id))
          resp  <- Ok(KanjiListResponse(Seq(kanji)).asJson)
        } yield resp

        response.handleErrorWith {
          case e @ KanjiNotFound(msg) =>
            Logger[F].error(e)(msg) *> NotFound(s"Kanji not found")
        }
    }

  /**
    * Path: <b>POST /user/sign_up</b>
    * <br/>
    * Creates a new user.
    */
  private[kanji] def recognizeKanjiHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "recognize" =>
        for {
          (strokes, count) <- req.as[RecognizeKanjiRequest].map(r => (r.strokes, r.count))
          result  <- kanjiService.recognize(strokes, count)
          resp    <- Ok(RecognizeKanjiResponse(result).asJson)
        } yield resp
    }

  def handlers: HttpRoutes[F] =
    kanjiListHandler <+>
      kanjiShortListHandler <+>
      kanjiHandler <+>
      recognizeKanjiHandler
}

object KanjiModule {

  def apply[F[_]: Effect: Logger](kanjiService: KanjiServiceAlgebra[F]): HttpRoutes[F] = {
    val module = new KanjiModule[F](kanjiService)
    // prefixes all of the routes with `/kanji/*`
    Router("/kanji" -> module.handlers)
  }
}
