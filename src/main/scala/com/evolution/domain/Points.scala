package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Points(value: Int) extends AnyVal

object Points {

  implicit val pointDecoder: Decoder[Points] =
    Decoder.forProduct1("points")(Points.apply)

  implicit val pointsEncoder: Encoder[Points] =
    Encoder.forProduct1("points")(points => points.value)
}
