package com.evolution.domain

import cats.data.{Validated, ValidatedNec}
import cats.implicits.catsSyntaxApply
import com.evolution.domain.errors.{ApplicationError, InvalidPassword}

final case class Password(value: String) extends AnyVal

object Password {

  def fromString(password: String): ValidatedNec[ApplicationError, Password] = {
    def isEmpty: ValidatedNec[ApplicationError, String] =
      Validated.cond(!password.isEmpty, password, InvalidPassword("Password can't be blank")).toValidatedNec

    def checkLen: ValidatedNec[ApplicationError, String] =
      Validated.cond(password.length >= 3 && password.length <= 20, password, InvalidPassword("Password length might be between 3 and 20")).toValidatedNec

    def validFirstAndLastSymbols: ValidatedNec[ApplicationError, String] =
      Validated.cond(!password.startsWith(" ") && !password.endsWith(" "), password, InvalidPassword("Password can't starts or ends with space")).toValidatedNec

    def validKindOfSymbols: ValidatedNec[ApplicationError, String] =
      Validated.cond(password.matches("^[A-Za-z0-9]*$"), password, InvalidPassword("Password contains invalid characters. Password might contains letters or numbers")).toValidatedNec

    (isEmpty *> checkLen *> validFirstAndLastSymbols *> validKindOfSymbols).map(password => Password(password))
  }
}