package me.oeshiro.tekio.external

import java.time.Instant

import me.oeshiro.tekio.domain.kanji._
import me.oeshiro.tekio.domain.user.{Settings, User, UserItem}
import me.oeshiro.tekio.external.kanji.KanjiFlat
import me.oeshiro.tekio.external.user.UserItemFlat
import me.oeshiro.tekio.utils.Id

object DTOConverters {
  def getFrequencies(flat: KanjiFlat): Map[String, Double] =
    List(
      flat.aozoraFreq.map("Aozora"   -> _),
      flat.twitterFreq.map("Twitter" -> _),
      flat.newsFreq.map("News"       -> _),
      flat.wikiFreq.map("Wikipedia"  -> _)
    ).flatten.toMap

  def getRankings(flat: KanjiFlat): Map[String, Int] =
    List(
      flat.aozoraRank.map("Aozora"   -> _),
      flat.twitterRank.map("Twitter" -> _),
      flat.newsRank.map("News"       -> _),
      flat.wikiRank.map("Wikipedia"  -> _)
    ).flatten.toMap

  def toKanji(dto: List[KanjiFlat]): Option[Kanji] = dto.headOption.map { first =>
    Kanji(
      id = Id[Kanji](first.id.toString),
      literal = first.literal.head,
      readings = dto.map(k => Reading(Id[Reading](k.readingId.toString), k.readingType, k.reading)).distinct,
      meanings = dto.map(k => Meaning(Id[Meaning](k.meaningId.toString), k.meaning)).distinct,
      frequencies = getFrequencies(first),
      rankings = getRankings(first),
      strokeCount = first.strokeCount,
      grade = GradeLevel.withNameInsensitive(first.grade),
      JLPT = JLPTLevel.withNameInsensitive(first.jlpt)
    )
  }

  def toKanjiList(dto: List[KanjiFlat]): List[Kanji] =
    dto
      .groupBy(_.id)
      .flatMap { case (_, k) => toKanji(k) }
      .toList
      .sortBy(_.id.toString)

  def toUserItem(dto: UserItemFlat): UserItem =
    UserItem(
      id = dto.id,
      userKanjiId = dto.userKanjiId,
      kanjiId = dto.kanjiId,
      reviewType = dto.reviewType,
      interval = dto.interval,
      nextReview = dto.nextReview,
      finished = dto.finished,
      frozen = dto.frozen,
      priority = dto.priority,
      answers = dto.answers,
      wins = dto.wins,
      fails = dto.fails,
      averageTime = dto.averageTime
    )

  case class UserRow(
      id: Id[User],
      email: String,
      username: String,
      picture: Option[String],
      createdAt: Instant,
      updatedAt: Instant,
      settings: Settings
  )

  def toUser(row: UserRow): User =
    User(row.id, row.email, row.username, row.picture, row.createdAt, row.updatedAt, row.settings)
}
