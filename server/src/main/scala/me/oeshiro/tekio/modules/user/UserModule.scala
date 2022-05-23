package me.oeshiro.tekio.modules.user

import java.time.Instant

import cats.effect.Effect
import cats.syntax.applicativeError._
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import cats.syntax.semigroupk._
import io.chrisdavenport.log4cats.Logger
import io.circe.generic.extras.auto._
import io.circe.syntax._
import me.oeshiro.tekio.domain.auth.Auth
import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiNotFound}
import me.oeshiro.tekio.domain.user._
import me.oeshiro.tekio.modules.user.domain._
import me.oeshiro.tekio.services.item.ItemServiceAlgebra
import me.oeshiro.tekio.services.user.UserServiceAlgebra
import me.oeshiro.tekio.utils.AuthUtils.{ProtectedRoute, protect}
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.JsonCodecs._
import me.oeshiro.tekio.utils.{AuthUtils, Id}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response}
import tsec.authentication._

import scala.concurrent.duration._

/**
  * The User module contains functions for handling user creation and modification.
  */
class UserModule[F[_]: Effect: Logger](
    userService: UserServiceAlgebra[F],
    itemService: ItemServiceAlgebra[F],
    auth: Auth[F]
) extends Http4sDsl[F] {

  object IndexParam  extends QueryParamDecoderMatcher[String]("ids")
  object FromParam   extends QueryParamDecoderMatcher[Long]("from")
  object ToParam     extends OptionalQueryParamDecoderMatcher[Long]("to")
  object OffsetParam extends OptionalQueryParamDecoderMatcher[Int]("offset")
  object CountParam  extends OptionalQueryParamDecoderMatcher[Int]("count")

  implicit class AuthErrorHandler(response: F[Response[F]]) {
    def handleCommonErrors: F[Response[F]] =
      response.handleErrorWith {
        case e @ UserNotFound(msg) =>
          Logger[F].error(e)(msg) *> NotFound("User not found")
        case e: UserNotAuthorized =>
          Logger[F].error(e)(e.getMessage) *> Unauthorized(
            AuthUtils.wwwHeader,
            "You are not authorized to access this resource"
          )
        // default internal error
        case e =>
          Logger[F].error(e)(e.getMessage) *> InternalServerError("Something went wrong")
      }
  }

  /**
    * Path: <b>POST /user/sign_up</b>
    * <br/>
    * Creates a new user.
    */
  private[user] def signUpHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "sign_up" =>
        val response = for {
          signup <- req.as[SignUpRequest]
          id     <- userService.createUser(signup.email, signup.password, signup.username)
          jwt    <- userService.signIn(signup.email, signup.password)
          resp   <- Ok(SignUpResponse(jwt).asJson)
        } yield resp

        response.handleErrorWith {
          case e @ UserAlreadyExists(msg) =>
            Logger[F].error(e)(msg) *> Conflict(s"A user with this email already exists")
        }.handleCommonErrors
    }

  /**
    * Path: <b>POST /user/sign_in</b>
    * <br/>
    * Authenticates a user given an email and a password.
    * In case they match, returns a JWT session token.
    */
  private[user] def signInHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case req @ POST -> Root / "sign_in" =>
        val response = for {
          signIn <- req.as[SignInRequest]
          jwt    <- userService.signIn(signIn.email, signIn.password)
          resp   <- Ok(SignInResponse(jwt))
        } yield resp

        response.handleErrorWith {
          case e @ UserNotFound(msg) =>
            Logger[F].error(e)(msg) *> BadRequest(s"Invalid email or password")
          case e @ UserAuthenticationFailed(msg) =>
            Logger[F].error(e)(msg) *> BadRequest(s"Invalid email or password")
        }.handleCommonErrors
    }

  /**
    * Path: <b>POST /user/profile/:id</b>
    * <br/>
    * Returns the profile info of a user.
    */
  private[user] def getProfileHandler: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "profile" / id =>
        val response = for {
          profile <- userService.findProfileById(Id[User](id))
          resp    <- Ok(profile.asJson)
        } yield resp

        response.handleErrorWith {
          case e: UserNotFound =>
            Logger[F].error(e)(e.getMessage) *> NotFound("User not found")
        }.handleCommonErrors
    }

  /**
    * Path: <b>GET /user</b>
    * <br/>
    * Returns the full model of a user.
    */
  private[user] def getUserHandler: ProtectedRoute[F] = protect {
    case GET -> Root asAuthed user =>
      val response = for {
        user <- userService.findById(user.id)
        resp <- Ok(user.asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>PUT /user/settings</b>
    * <br/>
    * Puts the user settings for a user.
    */
  private[user] def putSettingsHandler: ProtectedRoute[F] = protect {
    case req @ PUT -> Root / "settings" asAuthed user =>
      val response = for {
        settings <- req.request.as[Settings]
        userId   <- userService.updateUserSettings(user.id, settings)
        _        <- itemService.setKanjiOrder(user.id, settings)
        updated  <- userService.findById(userId)
        resp     <- Ok(updated.asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>PUT /user/profile</b>
    * <br/>
    * Puts the user profile info for a user.
    */
  private[user] def putProfileHandler: ProtectedRoute[F] = protect {
    case req @ PUT -> Root / "profile" asAuthed user =>
      val response = for {
        profile <- req.request.as[Profile]
        sanitized = profile.copy(picture = None)
        userId  <- userService.updateUserProfile(user.id, sanitized)
        updated <- userService.findProfileById(userId)
        resp    <- Ok(updated.asJson)
      } yield resp

      response.handleErrorWith {
        case e @ InvalidProfile(msg) =>
          Logger[F].error(e)(msg) *> BadRequest(msg)
      }.handleCommonErrors
  }

  /**
    * Path: <b>POST /user/lessons</b>
    * <br/>
    * Finishes the study session by unlocking the learned kanji.
    */
  private[user] def postLessonsHandler: ProtectedRoute[F] = protect {
    case req @ POST -> Root / "lessons" asAuthed user =>
      val response = for {
        request  <- req.request.as[PostLessonsRequest]
        kanji     = request.unlocked.toSet
        timeSpent = request.spent.millis
        _        <- itemService.finishStudySession(user.id, kanji, timeSpent)
        unlocked <- itemService.getUserKanji(user.id, kanji, None, None)
        resp     <- Ok(GetUserKanjiResponse(unlocked).asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>POST /user/unlock</b>
    * <br/>
    * Unlocks the selected kanji regardless of the study queue.
    */
  private[user] def unlockKanjiHandler: ProtectedRoute[F] = protect {
    case req @ POST -> Root / "unlock" asAuthed user =>
      val response = for {
        kanji    <- req.request.as[UnlockRequest].map(_.unlocked.toSet)
        unlocked <- itemService.unlockKanji(user.id, kanji)
        resp     <- Ok(GetUserKanjiResponse(unlocked).asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>GET /user/answers</b>
    * <br/>
    * Returns the user's answers from the specified time interval.
    */
  private[user] def getUserAnswers: ProtectedRoute[F] = protect {
    case GET -> Root / "answers" :? FromParam(fromMs) +& ToParam(toMs) +& OffsetParam(offset) +& CountParam(count) asAuthed user =>
      val from = Instant.ofEpochMilli(fromMs)
      val to   = toMs.map(Instant.ofEpochMilli)
      val response = for {
        answers <- itemService.getUserAnswers(user.id, from, to, offset, count)
        resp    <- Ok(GetAnswersResponse(answers).asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>POST /user/answers</b>
    * <br/>
    * Finishes the review session by persisting the user's answers
    * and rescheduling their items.
    */
  private[user] def postUserAnswers: ProtectedRoute[F] = protect {
    case req @ POST -> Root / "answers" asAuthed user =>
      val response = for {
        answers <- req.request.as[PostAnswersRequest].map(_.answers)
        updated <- itemService.finishReviewSession(user.id, answers)
        resp    <- Ok(GetUserKanjiResponse(updated).asJson)
      } yield resp

      response.handleCommonErrors
  }

  /**
    * Path: <b>GET /user/kanji?ids=1;2;3;4&offset=:int:&count=:int:</b>
    * <br/>
    * Returns a list of `count` full kanji starting from the offset
    * with the complete user data about them.
    */
  private[user] def getUserKanjiHandler: ProtectedRoute[F] = protect {
    case GET -> Root / "kanji" :? IndexParam(ids) +& OffsetParam(offset) +& CountParam(count) asAuthed user =>
      val indices = ids.some.filterNot(_.isEmpty).map(_.split(',').map(Id[Kanji]).toSet).getOrElse(Set.empty)
      for {
        userKanji <- itemService.getUserKanji(user.id, indices, offset, count)
        resp      <- Ok(GetUserKanjiResponse(userKanji).asJson)
      } yield resp
  }

  /**
    * Path: <b>GET /user/kanji/走</b>
    * <br/>
    * Returns a list of `count` full kanji starting from the offset
    * with the complete user data about them.
    */
  private[user] def getUserKanjiByLiteralHandler: ProtectedRoute[F] = protect {
    case GET -> Root / "kanji" / literal asAuthed user =>
      val response = for {
        userKanji <- itemService.getUserKanji(user.id, literal)
        resp      <- Ok(GetUserKanjiResponse(Seq(userKanji)).asJson)
      } yield resp

      response.handleErrorWith {
        case e @ KanjiNotFound(msg) =>
          Logger[F].error(e)(msg) *> NotFound(s"Kanji not found")
      }.handleCommonErrors
  }

  def handlers: HttpRoutes[F] =
    signUpHandler <+>
      signInHandler <+>
      getProfileHandler <+>
      SecuredRequestHandler(auth).liftService(
        getUserHandler <+>
          putSettingsHandler <+>
          putProfileHandler <+>
          postLessonsHandler <+>
          getUserAnswers <+>
          postUserAnswers <+>
          unlockKanjiHandler <+>
          getUserKanjiHandler <+>
          getUserKanjiByLiteralHandler
      )
}

object UserModule {

  def apply[F[_]: Effect: Logger](
      userService: UserServiceAlgebra[F],
      itemService: ItemServiceAlgebra[F],
      auth: Auth[F]
  ): HttpRoutes[F] = {
    val module = new UserModule[F](userService, itemService, auth)
    // prefixes all of the routes with `/user/*`
    Router("/user" -> module.handlers)
  }
}
