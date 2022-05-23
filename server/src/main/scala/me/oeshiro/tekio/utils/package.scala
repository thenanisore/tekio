package me.oeshiro.tekio

import shapeless.tag.@@

package object utils {
  type Id[A] = String @@ A
}
