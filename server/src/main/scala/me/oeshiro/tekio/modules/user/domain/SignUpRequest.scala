package me.oeshiro.tekio.modules.user.domain

case class SignUpRequest(email: String, password: String, username: Option[String])
