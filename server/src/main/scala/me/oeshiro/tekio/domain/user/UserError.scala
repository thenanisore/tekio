package me.oeshiro.tekio.domain.user

abstract class UserError(msg: String)            extends Exception(msg)
case class UserNotFound(msg: String)             extends UserError(s"Could not find user with $msg")
case class UserAlreadyExists(msg: String)        extends UserError(s"User already exists with $msg")
case class UserAuthenticationFailed(msg: String) extends UserError(s"Couldn't authenticate a user: $msg")
case class UserNotAuthorized()                   extends UserError(s"User is not authorized")
case class InvalidProfile(msg: String)           extends UserError(s"Profile can't be updated: $msg")
