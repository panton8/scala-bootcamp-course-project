package com.evolution.domain

import cats.data.{Validated, ValidatedNec}
import cats.implicits.catsSyntaxApply
import com.evolution.domain.errors.{ApplicationError, InvalidUserName}


final case class Name(value: String) extends AnyVal

object Name {

  def fromString(userName: String): ValidatedNec[ApplicationError, Name] = {
    def isEmpty: ValidatedNec[ApplicationError, String] =
      Validated.cond(!userName.isEmpty, userName, InvalidUserName("Nickname can't be blank")).toValidatedNec

    def checkLen: ValidatedNec[ApplicationError, String] =
      Validated.cond(userName.length >= 5 && userName.length <= 15, userName, InvalidUserName("Nickname length might be between 5 and 15")).toValidatedNec

    def validFirstAndLastSymbols: ValidatedNec[ApplicationError, String] =
      Validated.cond(!userName.startsWith(" ") && !userName.endsWith(" "), userName, InvalidUserName("Nickname can't starts or ends with space")).toValidatedNec

    (isEmpty *> checkLen *> validFirstAndLastSymbols).map(userName => Name(userName))
  }
}