package me.oeshiro.tekio.domain.user

import me.oeshiro.tekio.domain.auth.Hash

final case class NewUser(email: String, hash: Hash, salt: String, username: String, defaults: Settings)
