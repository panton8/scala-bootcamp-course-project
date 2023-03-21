package com.evolution.domain

final case class User(
  id: Id,
  email: Email,
  password: Password,
  access: Access = Base,
  budget: Budget = Budget(100.0)
)
