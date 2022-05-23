package me.oeshiro.tekio.external.kanji

import cats.data.{NonEmptyList, OptionT}
import cats.effect.{Resource, Sync}
import cats.syntax.apply._
import cats.syntax.applicative._
import cats.syntax.functor._
import doobie._
import doobie.implicits._
import io.chrisdavenport.log4cats.Logger
import me.oeshiro.tekio.domain.kanji.{GradeLevel, JLPTLevel, Kanji, KanjiShort, ReadingType}
import me.oeshiro.tekio.external.DTOConverters
import me.oeshiro.tekio.utils.Id
import me.oeshiro.tekio.utils.Id._
import me.oeshiro.tekio.utils.LoggableEffectOps._

class KanjiRepository[F[_]: Logger](xa: Transactor[F])(implicit F: Sync[F]) extends KanjiRepositoryAlgebra[F] {

  implicit val readingTypeMeta: Meta[ReadingType] = Meta[String].imap(ReadingType.withNameInsensitive)(_.toString)
  implicit val jlptLevelMeta: Meta[JLPTLevel] = Meta[String].imap(JLPTLevel.withNameInsensitive)(_.toString)
  implicit val gradeLevelMeta: Meta[GradeLevel] = Meta[String].imap(GradeLevel.withNameInsensitive)(_.toString)

  override def getFull(id: Id[Kanji]): OptionT[F, Kanji] = OptionT {
    sql"""
      | select k.id, k.literal, k.stroke_count,
      | k.aozora_freq, k.news_freq, k.twitter_freq, k.wiki_freq,
      | k.aozora_rank, k.news_rank, k.twitter_rank, k.wiki_rank,
      | k.grade, k.jlpt, r.id, r.reading, r.type, m.id, m.meaning
      | from kanji as k
      | inner join kanji_readings as r on k.id = r.kanji_id
      | inner join kanji_meanings as m on k.id = m.kanji_id
      | where k.id = ${id.toInt}
    """.stripMargin
      .query[KanjiFlat]
      .to[List]
      .transact(xa)
      .map(DTOConverters.toKanji)
      .log(kanji => s"Fetched full kanji by id=$id, $kanji")
  }

  override def getFull(ids: Seq[Id[Kanji]], offset: Option[Int], count: Option[Int]): F[Seq[Kanji]] = {
    val start   = offset.getOrElse(0)
    val end     = count.map(_ + start).getOrElse(ids.length)
    val indices = ids.slice(start, end).map(_.toInt).toList
    indices match {
      case h :: t =>
        val fullQuery = fr"""
            | select k.id, k.literal, k.stroke_count,
            | k.aozora_freq, k.news_freq, k.twitter_freq, k.wiki_freq,
            | k.aozora_rank, k.news_rank, k.twitter_rank, k.wiki_rank,
            | k.grade, k.jlpt, r.id, r.reading, r.type, m.id, m.meaning
            | from kanji as k
            | inner join kanji_readings as r on k.id = r.kanji_id
            | inner join kanji_meanings as m on k.id = m.kanji_id
            | where """.stripMargin ++ Fragments.in(fr"k.id", NonEmptyList(h, t))
        fullQuery
          .query[KanjiFlat]
          .to[List]
          .transact(xa)
          .map(DTOConverters.toKanjiList)
          .log(_ => s"Fetched ${indices.length} full kanji by ids with count=$count, offset=$offset")
          .map(_.toSeq)
      case _ =>
        Logger[F].info("No kanji were fetched, returning Nil") *> Seq.empty[Kanji].pure[F]
    }
  }

  override def getFullByLiteral(literal: String): OptionT[F, Kanji] = OptionT {
    sql"""
         | select k.id, k.literal, k.stroke_count,
         | k.aozora_freq, k.news_freq, k.twitter_freq, k.wiki_freq,
         | k.aozora_rank, k.news_rank, k.twitter_rank, k.wiki_rank,
         | k.grade, k.jlpt, r.id, r.reading, r.type, m.id, m.meaning
         | from kanji as k
         | inner join kanji_readings as r on k.id = r.kanji_id
         | inner join kanji_meanings as m on k.id = m.kanji_id
         | where k.literal = ${literal}
    """.stripMargin
      .query[KanjiFlat]
      .to[List]
      .transact(xa)
      .map(DTOConverters.toKanji)
      .log(kanji => s"Fetched full kanji by literal=$literal, $kanji")
  }

  override def getAll(offset: Option[Int], count: Option[Int]): F[Seq[KanjiShort]] =
    count match {
      case Some(0) => Logger[F].info("No kanji were fetched, returning Nil") *> Seq.empty[KanjiShort].pure[F]
      case _ =>
        sql"""
             | select k.id, k.literal, k.grade, k.jlpt,
             |   (select string_agg(ms.meaning, ', ') from
             |     (select meaning
             |      from kanji_meanings as km
             |      where km.kanji_id = k.id
             |      limit 2) as ms
             |    ) as meanings
             | from kanji as k
             | where k.id > ${offset.getOrElse(0)}
             | group by k.id
             | order by k.id
             | limit $count
          """.stripMargin
          .query[KanjiShort]
          .to[Seq]
          .transact(xa)
          .log(ks => s"Fetched ${ks.length} short kanji with count=$count, offset=$offset")
    }
}

object KanjiRepository {

  def create[F[_]: Sync: Logger](xa: Transactor[F]): Resource[F, KanjiRepositoryAlgebra[F]] =
    Resource.pure(new KanjiRepository[F](xa))
}
