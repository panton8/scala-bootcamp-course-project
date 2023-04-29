package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Id(value: Int) extends AnyVal

object Id {

  implicit val decodeId: Decoder[Id] =
    Decoder.forProduct1("id")(Id.apply)

  implicit val encodeId: Encoder[Id] =
    Encoder.forProduct1("id")(id => id.value)
}
