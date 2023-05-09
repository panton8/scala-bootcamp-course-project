package com.evolution.domain

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Email(value: String) extends AnyVal

object Email {
  implicit val jsonDecoder: Decoder[Email] = cursor =>
    for {
      email <- cursor.get[String]("email")
    } yield Email(email)

  implicit val jsonEncoder: Encoder[Email] = Encoder.instance {
    case Email(email) => Json.obj(
      "email" -> email.asJson,
    )
  }
}
