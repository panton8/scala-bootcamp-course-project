package com.evolution.domain

import com.evolution.domain.Access.Base
import doobie.util.{Read, Write}

final case class User(
  id: Id,
  userName: Name,
  email: Email,
  password: Password,
  access: Access = Base,
  budget: Budget = Budget(100.0)
)

object User {
  implicit val userRead: Read[User] = Read[(Int, String, String, String, Access, Double)].map {
    case (id, name, email, password, access, budget) =>
      new User(
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
