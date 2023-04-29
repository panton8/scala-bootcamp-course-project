package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Email(value: String) extends AnyVal

object Email {

  implicit val decodeEmail: Decoder[Email] =
    Decoder.forProduct1("email")(Email.apply)

  implicit val encodeEmail: Encoder[Email] =
    Encoder.forProduct1("email")(email => email.value)
}
