package me.oeshiro.tekio.config

import cats.effect.{Async, ContextShift, Resource, Sync}
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

/** Database connection properties. */
final case class DatabaseConfig(url: String, driver: String, user: String, password: String)

case object DatabaseConfig {

  def initializeDataSource[F[_]: Sync](config: DatabaseConfig): F[HikariDataSource] =
    Sync[F].delay {
      Class.forName("org.postgresql.Driver")
      val ds = new HikariDataSource()
      ds.setJdbcUrl(config.url)
      ds.setUsername(config.user)
      ds.setPassword(config.password)
      ds.getConnection // eager initialization
      ds
    }

  def dbTransactor[F[_]: Async: ContextShift](config: DatabaseConfig): Resource[F, HikariTransactor[F]] =
    for {
      ds <- Resource.liftF(initializeDataSource(config))
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      te <- ExecutionContexts.cachedThreadPool[F]
    } yield HikariTransactor(ds, ce, te)
}
