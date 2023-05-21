package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Surname(value: String) extends AnyVal

object Surname {

  implicit val decodeSurname: Decoder[Surname] =
    Decoder.forProduct1("surName")(Surname.apply)

  implicit val encodeSurname: Encoder[Surname] =
    Encoder.forProduct1("surName")(surName => surName.value)
}