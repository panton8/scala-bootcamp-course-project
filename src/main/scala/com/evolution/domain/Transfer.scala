package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Transfer(value: Int) extends AnyVal

object Transfer {

  implicit val transferDecoder: Decoder[Transfer] =
    Decoder.forProduct1("transfers")(Transfer.apply)

  implicit val transferEncoder: Encoder[Transfer] =
    Encoder.forProduct1("transfers")(trans => trans.value)
}