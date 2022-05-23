package me.oeshiro.tekio.utils

import cats.Eq
import cats.instances.string._
import cats.syntax.eq._
import doobie.util.Meta
import io.circe.{Decoder, Encoder}

object Id {
  def apply[A](id: String): Id[A] = id.asInstanceOf[Id[A]]

  implicit def IdEq[A]: Eq[Id[A]] =
    (x: Id[A], y: Id[A]) => x.toString === y.toString

  implicit def ordering[A]: Ordering[Id[A]] = Ordering.by(_.toString)

  // circe implicits
  implicit def IdEncoder[A]: Encoder[Id[A]] = Encoder.encodeString.contramap(_.toString)
  implicit def IdDecoder[A]: Decoder[Id[A]] = Decoder.decodeString.map(Id.apply[A])

  // doobie implicits
  implicit def idMeta[A]: Meta[Id[A]] = Meta[Int].imap(id => Id.apply[A](id.toString))(_.toInt)
}
