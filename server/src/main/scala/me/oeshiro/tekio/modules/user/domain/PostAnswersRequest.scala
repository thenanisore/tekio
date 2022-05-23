package me.oeshiro.tekio.modules.user.domain

import me.oeshiro.tekio.domain.user.NewUserAnswer

case class PostAnswersRequest(answers: Seq[NewUserAnswer])