package me.oeshiro.tekio

import cats.effect._
import cats.syntax.functor._
import cats.syntax.semigroupk._
import io.chrisdavenport.log4cats.SelfAwareLogger
import io.chrisdavenport.log4cats.log4s.Log4sLogger
import io.circe.config.parser
import me.oeshiro.tekio.config._
import me.oeshiro.tekio.external.kanji.KanjiRepository
import me.oeshiro.tekio.external.user.{ItemRepository, UserRepository}
import me.oeshiro.tekio.modules.kanji.KanjiModule
import me.oeshiro.tekio.modules.user.UserModule
import me.oeshiro.tekio.modules.util.UtilModule
import me.oeshiro.tekio.services.auth.AuthService
import me.oeshiro.tekio.services.item.ItemService
import me.oeshiro.tekio.services.kanji.KanjiService
import me.oeshiro.tekio.services.user.UserService
import me.oeshiro.tekio.services.util.UtilService
import me.oeshiro.tekio.utils.{AuthUtils, ResponseEnvelope}
import monix.eval.{Task, TaskApp}
import org.http4s.Http
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, Logger => LoggerMiddleware}
import org.http4s.server.staticcontent._
import org.http4s.server.{Router, Server => HttpServer}
import tsec.passwordhashers.jca.HardenedSCrypt

object Server extends TaskApp {

  /** Initializes an application server with all of the services and API routes. */
  def createServer[F[_]: ConcurrentEffect: ContextShift: Timer]: Resource[F, HttpServer[F]] = {
    implicit val logger: SelfAwareLogger[F] = Log4sLogger.createByName[F]("tekio")
    for {
      config     <- Resource.liftF(parser.decodePathF[F, Config]("tekio"))
      cors       <- Resource.liftF(CorsConfig.getConfig[F](config.cors))
      xa         <- DatabaseConfig.dbTransactor[F](config.db)
      userRepo   <- UserRepository.create[F](xa)
      kanjiRepo  <- KanjiRepository.create[F](xa)
      itemRepo   <- ItemRepository.create[F](xa)
      signingKey <- Resource.liftF(AuthUtils.generateSigningKey[F](config.auth.secret))
      hasher       = HardenedSCrypt.syncPasswordHasher[F]
      authService  = new AuthService[F](config.auth, hasher, signingKey, userRepo)
      utilService  = new UtilService[F]
      kanjiService = new KanjiService[F](kanjiRepo)
      itemService  = new ItemService[F](itemRepo, kanjiService)
      userService  = new UserService[F](config.user, authService, itemService, userRepo)
      api = UtilModule[F](utilService) <+>
            UserModule[F](userService, itemService, authService.auth) <+>
            KanjiModule[F](kanjiService)
      mw = (LoggerMiddleware.apply[F](logHeaders = true, logBody = false) _)
        .compose(ResponseEnvelope.apply[F])
        .compose(CORS.apply[F, F](_: Http[F, F], cors))
      static = fileService(FileService.Config[F]("./static"))
      httpApp = Router[F](mappings =
        "/static" -> static,
        "/" -> api
      ).orNotFound
      server <- BlazeServerBuilder[F]
        .bindHttp(config.server.port, config.server.host)
        .withHttpApp(mw(httpApp))
        .resource
    } yield server
  }

  /** Runs the server. */
  def run(args: List[String]): Task[ExitCode] =
    createServer[Task].use(_ => Task.never).as(ExitCode.Success)

}
