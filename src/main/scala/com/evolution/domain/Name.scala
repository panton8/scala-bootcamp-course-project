package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Name(value: String) extends AnyVal

object Name {
  implicit val decodeName: Decoder[Name] =
    Decoder.forProduct1("name")(Name.apply)

  implicit val encodeName: Encoder[Name] =
    Encoder.forProduct1("name")(name => name.value)
}
