package com.evolution.domain

import com.evolution.domain.Access.Base
import doobie.util.{Read, Write}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class User(
  id: Id,
  userName: Name,
  email: Email,
  password: Password,
  access: Access = Base,
  budget: Budget = Budget(100.0)
)

object User {

  implicit val jsonDecoder: Decoder[User] = cursor =>
    for {
      id       <- cursor.get[Int]("id")
      userName <- cursor.get[String]("userName")
      email    <- cursor.get[String]("email")
      password <- cursor.get[String]("password")
      access   <- cursor.get[String]("access")
      budget   <- cursor.get[Double]("budget")
    } yield User(Id(id), Name(userName), Email(email), Password(password), Access.withName(access), Budget(budget))

  implicit val jsonEncoder: Encoder[User] = Encoder.instance {
    case User(id, userName, email, password, access, budget) => Json.obj(
      "id" -> id.value.asJson,
      "userName"  ->  userName.value.asJson,
      "email"     -> email.value.asJson,
      "password"  -> password.value.asJson,
      "access"    -> access.entryName.asJson,
      "budget"    -> budget.value.asJson
    )
  }

  implicit val userRead: Read[User] = Read[(Int, String, String, String, Access, Double)].map {
    case (id, name, email, password, access, budget) =>
      User(
        Id(id),
        Name(name),
        Email(email),
        Password(password),
        access,
        Budget(budget))
  }

  implicit val userWrite: Write[User] = Write[(Int, String, String, String, Access, Double)].contramap {
    user =>
    (
      user.id.value,
      user.userName.value,
      user.email.value,
      user.password.value,
      user.access,
      user.budget.value)
  }
}