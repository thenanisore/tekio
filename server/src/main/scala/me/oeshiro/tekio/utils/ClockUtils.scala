package me.oeshiro.tekio.utils

import java.time.{Instant, LocalDate, ZoneOffset}

import cats.Functor
import cats.effect.Clock
import cats.syntax.functor._

import scala.concurrent.duration.MILLISECONDS

object ClockUtils {

  /** Adds an extension method that returns a timestamp by means of cats.effect.Clock. */
  implicit class ClockOps[F[_]: Functor](clock: Clock[F]) {
    def instant: F[Instant] = clock.realTime(MILLISECONDS).map(Instant.ofEpochMilli)

    def startOfDay: F[Instant] = instant.map { i =>
      val today = LocalDate.from(i.atOffset(ZoneOffset.UTC))
      today.atStartOfDay().toInstant(ZoneOffset.UTC)
    }
  }
}