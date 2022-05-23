package me.oeshiro.tekio.services.kanji

import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiShort}
import me.oeshiro.tekio.utils.Id

trait KanjiServiceAlgebra[F[_]] {

  /**
    * Returns a kanji from a repository by an id.
    */
  def findKanjiById(id: Id[Kanji]): F[Kanji]

  /**
    * Returns a kanji from a repository by its literal.
    */
  def findKanjiByLiteral(literal: String): F[Kanji]

  /**
    * Returns a list of kanji from a repository by their ids.
    */
  def getKanjiList(ids: Seq[Id[Kanji]], offset: Option[Int], count: Option[Int]): F[Seq[Kanji]]

  /**
    * Returns a part of the list of `count` kanji specified by an offset.
    */
  def getKanjiShortList(offset: Option[Int], count: Option[Int]): F[Seq[KanjiShort]]

  /**
    * Returns a list of `count` most similar kanji.
    */
  def recognize(strokes: Seq[Seq[(Double, Double)]], count: Int): F[Seq[KanjiShort]]
}
