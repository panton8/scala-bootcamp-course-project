package com.evolution.domain.errors

import io.circe.{Decoder, Encoder}

sealed abstract class ApplicationError(msg: String) extends Throwable(msg)

final case class SuchUserAlreadyExist(msg: String) extends ApplicationError(msg)
final case object SuchUserDoesNotExist extends ApplicationError("Such user doesn't exist")
final case object SuchTeamDoesNotExist extends ApplicationError("Such team doesn't exist")
final case object PriseMoreThanBudget extends ApplicationError("Players prise more than your budget")
final case object NonAvailableTransfer extends ApplicationError("No available transfers")
final case class AuthenticationError(msg: String) extends ApplicationError(msg)

object AuthenticationError {
  implicit val userRegDecoder: Decoder[AuthenticationError] =
    Decoder.forProduct1("error")(AuthenticationError.apply)

  implicit val userRegEncoder: Encoder[AuthenticationError] =
    Encoder.forProduct1("error")(e => e.msg)
}