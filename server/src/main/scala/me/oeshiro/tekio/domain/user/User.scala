package me.oeshiro.tekio.domain.user

import java.time.Instant

import me.oeshiro.tekio.domain.auth.Credentials
import me.oeshiro.tekio.domain.kanji.Kanji
import me.oeshiro.tekio.utils.Id

case class User(
    id: Id[User],
    email: String,
    username: String,
    picture: Option[String],
    createdAt: Instant,
    updatedAt: Instant,
    settings: Settings,
    unlocked: Seq[Id[Kanji]] = Seq.empty,
    lessonQueue: Seq[Id[Kanji]] = Seq.empty,
    reviewQueue: Seq[(Id[UserItem], Id[Kanji])] = Seq.empty
)
