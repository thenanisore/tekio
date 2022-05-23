package me.oeshiro.tekio.modules.user.domain

import me.oeshiro.tekio.domain.user.UserAnswer

case class GetAnswersResponse(answers: Seq[UserAnswer])

