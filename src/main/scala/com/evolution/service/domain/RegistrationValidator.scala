package com.evolution.service.domain

import cats.data.ValidatedNec
import cats.effect.IO
import cats.implicits.catsSyntaxTuple3Semigroupal
import com.evolution.domain.errors.ApplicationError
import com.evolution.domain.{Email, Name, Password}
import com.evolution.http.domain.UserRegistration

object RegistrationValidator {

  def validate(userName: String, email: String, password: String): IO[ValidatedNec[ApplicationError, UserRegistration]] = {
    IO.pure((Name.fromString(userName),
      Email.fromString(email),
      Password.fromString(password)
    ).mapN(UserRegistration.apply))
  }
}