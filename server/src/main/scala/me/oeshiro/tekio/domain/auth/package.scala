package me.oeshiro.tekio.domain

import me.oeshiro.tekio.domain.user.User
import me.oeshiro.tekio.utils.Id
import tsec.authentication.JWTAuthenticator
import tsec.mac.jca.HMACSHA256
import tsec.passwordhashers.jca.HardenedSCrypt
import tsec.passwordhashers.{PasswordHash, PasswordHasher}

package object auth {

  // the algorithm the user passwords are hashed with
  type HashAlgorithm = HardenedSCrypt

  type Hash = PasswordHash[HashAlgorithm]

  type TekioHasher[F[_]] = PasswordHasher[F, HashAlgorithm]

  type Auth[F[_]] = JWTAuthenticator[F, Id[User], User, JWTSigningKey]

  type JWTSigningKey = HMACSHA256
}
