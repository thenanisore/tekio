package me.oeshiro.tekio.domain.auth

import me.oeshiro.tekio.domain.user.User
import me.oeshiro.tekio.utils.Id

case class Credentials(userId: Id[User], email: String, hash: Hash, salt: String)
