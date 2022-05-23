package me.oeshiro.tekio.services.kanji

import cats.MonadError
import cats.syntax.applicative._
import cats.syntax.eq._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.instances.int._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiNotFound, KanjiShort}
import me.oeshiro.tekio.external.kanji.KanjiRepositoryAlgebra
import me.oeshiro.tekio.utils.Id

class KanjiService[F[_]: Logger](kanjiRepo: KanjiRepositoryAlgebra[F])(
    implicit F: MonadError[F, Throwable]
) extends KanjiServiceAlgebra[F] {

  /**
    * Returns a full kanji from the repository by an id.
    */
  override def findKanjiById(id: Id[Kanji]): F[Kanji] =
    kanjiRepo.getFull(id).value.flatMap {
      case Some(kanji) => kanji.pure[F]
      case None       => F.raiseError(KanjiNotFound(s"id=$id"))
    }

  /**
    * Returns a full kanji from the repository by its literal.
    */
  override def findKanjiByLiteral(literal: String): F[Kanji] =
    if (literal.length =!= 1) F.raiseError(KanjiNotFound(s"literal=$literal"))
    else {
      kanjiRepo.getFullByLiteral(literal).value.flatMap {
        case Some(kanji) => kanji.pure[F]
        case None => F.raiseError(KanjiNotFound(s"literal=$literal"))
      }
    }

  /**
    * Returns a list of kanji from a repository by their ids.
    */
  override def getKanjiList(ids: Seq[Id[Kanji]], offset: Option[Int], count: Option[Int]): F[Seq[Kanji]] = {
    kanjiRepo.getFull(ids, offset, count)
  }

  /**
    * Returns a user from a repository by an id.
    */
  override def getKanjiShortList(offset: Option[Int], count: Option[Int]): F[Seq[KanjiShort]] =
    kanjiRepo.getAll(offset, count).map(_.toSeq)

  /**
    * Returns a list of `count` most similar kanji.
    */
  override def recognize(strokes: Seq[Seq[(Double, Double)]], count: Int): F[Seq[KanjiShort]] = {
    // TODO: implement recognition
    Seq.empty[KanjiShort].pure[F]
  }
}
