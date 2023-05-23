package com.evolution.domain

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Id(value: Int) extends AnyVal

object Id {

  implicit val jsonDecoder: Decoder[Id] = cursor =>
    for {
      id <- cursor.get[Int]("id")
    } yield Id(id)

  implicit val jsonEncoder: Encoder[Id] = Encoder.instance {
    case Id(id) => Json.obj(
      "id" -> id.asJson,
    )
  }
}