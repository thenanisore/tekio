package me.oeshiro.tekio.utils

import cats.Monad
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.syntax.flatMap._
import io.chrisdavenport.log4cats.Logger

object LoggableEffectOps {

  implicit class LoggableEffect[F[_] : Monad : Logger, A](ef: F[A]) {
    def log(formMessage: A => String): F[A] = {
      ef.flatMap(a => Logger[F].info(formMessage(a)) *> a.pure[F])
    }

    def logMessage(message: String): F[A] = {
      ef.flatMap(a => Logger[F].info(message) *> a.pure[F])
    }
  }
}

