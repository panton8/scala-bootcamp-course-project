package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Password(value: String) extends AnyVal

object Password {

  implicit val decodePassword: Decoder[Password] =
    Decoder.forProduct1("password")(Password.apply)

  implicit val encodePassword: Encoder[Password] =
    Encoder.forProduct1("password")(p => p.value)
}
