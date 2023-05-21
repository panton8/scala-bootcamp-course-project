package com.evolution.domain

import cats.data.{Validated, ValidatedNec}
import cats.implicits.catsSyntaxApply
import com.evolution.domain.errors.{ApplicationError, InvalidEmail}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Email (value: String) extends AnyVal

object Email {

  def fromString(email: String): ValidatedNec[ApplicationError, Email] = {
    def isEmpty:ValidatedNec[ApplicationError, String] =
      Validated.cond(!email.isEmpty, email, InvalidEmail("Password can't be blank")).toValidatedNec

    def correctSymbols: ValidatedNec[ApplicationError, String] = {
      Validated.cond(email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") |
        email.isEmpty, email, InvalidEmail("Invalid email")).toValidatedNec
    }
    (isEmpty *> correctSymbols).map(email => Email(email))
  }

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
