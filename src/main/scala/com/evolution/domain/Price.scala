package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Price(value: Double) extends AnyVal

object Price {

  implicit val decodeAccess: Decoder[Price] =
    Decoder.forProduct1("price")(Price.apply)

  implicit val encodeAccess: Encoder[Price] =
    Encoder.forProduct1("price")(price => price.value)
}