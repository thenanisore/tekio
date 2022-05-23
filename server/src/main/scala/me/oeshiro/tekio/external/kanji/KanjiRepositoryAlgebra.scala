package me.oeshiro.tekio.external.kanji

import cats.data.OptionT
import me.oeshiro.tekio.domain.kanji.{Kanji, KanjiShort}
import me.oeshiro.tekio.utils.Id

trait KanjiRepositoryAlgebra[F[_]] {

  /**
    * Returns the full information about a kanji by its id.
    */
  def getFull(id: Id[Kanji]): OptionT[F, Kanji]

  /**
    * Returns the full information about a list of kanji by their ids (with pagination).
    */
  def getFull(ids: Seq[Id[Kanji]], offset: Option[Int], count: Option[Int]): F[Seq[Kanji]]

  /**
    * Returns the full information about a kanji by its literal.
    */
  def getFullByLiteral(literal: String): OptionT[F, Kanji]

  /**
    * Returns the basic information about all kanji (with pagination).
    */
  def getAll(offset: Option[Int], count: Option[Int]): F[Seq[KanjiShort]]
}
