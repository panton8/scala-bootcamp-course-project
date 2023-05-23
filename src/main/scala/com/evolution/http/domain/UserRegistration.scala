package com.evolution.http.domain

import cats.implicits._
import io.circe._
import com.evolution.domain.{Email, Name, Password}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

final case class UserRegistration(userName: Name, email: Email, password: Password)

object UserRegistration {
  implicit val jsonDecoder: Decoder[UserRegistration] = cursor =>
    for {
      userName <- cursor.get[String]("userName")
      email    <- cursor.get[String]("email")
      password <- cursor.get[String]("password")
    } yield UserRegistration(Name(userName), Email(email), Password(password))

  implicit val jsonEncoder: Encoder[UserRegistration] = Encoder.instance {
    case UserRegistration(id, email, password) => Json.obj(
      "id" -> id.value.asJson,
      "email"     -> email.value.asJson,
      "password"  -> password.value.asJson
    )
  }
}