package me.oeshiro.tekio.config

/** Application configuration. */
final case class Config(
    db: DatabaseConfig,
    server: ServerConfig,
    auth: AuthConfig,
    cors: CorsConfig,
    user: UserConfig
)
