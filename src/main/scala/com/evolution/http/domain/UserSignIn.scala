package com.evolution.http.domain

import com.evolution.domain.{Email, Password}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class UserSignIn(email: Email, password: Password)

object UserSignIn {

  implicit val jsonDecoder: Decoder[UserSignIn] = cursor =>
    for {
      email <- cursor.get[String]("email")
      password <- cursor.get[String]("password")
    } yield UserSignIn(Email(email), Password(password))

  implicit val jsonEncoder: Encoder[UserSignIn] = Encoder.instance {
    case UserSignIn(email, password) => Json.obj(
      "email" -> email.value.asJson,
      "password" -> password.value.asJson
    )
  }
}