package me.oeshiro.tekio.config

import me.oeshiro.tekio.domain.user.Settings

/** User configuration such as default settings for new users. */
final case class UserConfig(defaults: Settings)

