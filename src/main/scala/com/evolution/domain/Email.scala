package com.evolution.domain

import com.evolution.domain.errors.SuchUserDoesNotExist
import io.circe.Decoder.Result
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}

final case class Email (value: String) extends AnyVal

object Email {

  /*def apply(email: String): Option[Email] =
    if (email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"))
      Some(new Email(email))
    else None*/

  implicit val jsonDecoder: Decoder[Email] = new Decoder[Email] {
    override def apply(cursor: HCursor): Result[Email] = cursor.get[String]("email") match {
      case Left(_) => Left(DecodingFailure("Error", cursor.history))
      case Right(email) => if (email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"))
        Right(Email(email))
      else
        Left(DecodingFailure("Error", cursor.history))
    }
  }

    implicit val jsonEncoder: Encoder[Email] = Encoder.instance {
      case Email(email) => Json.obj(
        "email" -> email.asJson,
      )
    }
}
