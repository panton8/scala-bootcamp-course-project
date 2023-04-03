package com.evolution.domain

import doobie.util.{Read, Write}
import AccessSyntax._

final case class User(
  id: Id,
  userName: Name,
  email: Email,
  password: Password,
  access: Access = Base,
  budget: Budget = Budget(100.0)
)

object User {
  implicit val userRead: Read[User] = Read[(Int, String, String, String, String, Double)].map {
    case (id, name, email, password, role, budget) =>
      new User(Id(id), Name(name), Email(email), Password(password), role.toAccess, Budget(budget))
  }

  implicit val userWrite: Write[User] = Write[(Int, String, String, String, String, Double)].contramap {
    user =>
    (
      user.id.value,
      user.userName.value,
      user.email.value,
      user.password.value,
      user.access.toString,
      user.budget.value)
  }
}
