package com.evolution.domain.errors

import io.circe.{Decoder, Encoder}

sealed abstract class ApplicationError(msg: String) extends Throwable(msg)

final case object SuchUserAlreadyExist extends ApplicationError("Such email or userName already exist")
final case object SuchUserDoesNotExist extends ApplicationError("Such user doesn't exist")
final case object SuchTeamDoesNotExist extends ApplicationError("Such team doesn't exist")
final case object SuchPlayerDoesNotExist extends ApplicationError("Such player doesn't exist")
final case object PriseMoreThanBudget extends ApplicationError("Players prise more than your budget")
final case object NonAvailableTransfer extends ApplicationError("No available transfers")
final case class AuthenticationError(msg: String) extends ApplicationError(msg)
final case object NotEnoughPlayers extends ApplicationError("Not enough players in your team")
final case object InvalidCaptain extends ApplicationError("Captain need to be from your team")
final case object DuplicatePlayers extends ApplicationError("Team can only consist of unique players")
final case object DuplicateTeam extends ApplicationError("You can have only one team")
final case object InvalidTeamPlayer extends ApplicationError("Such player not from your team")
final case object InvalidChange extends ApplicationError("New player is already in your team")

object AuthenticationError {
  implicit val userRegDecoder: Decoder[AuthenticationError] =
    Decoder.forProduct1("error")(AuthenticationError.apply)

  implicit val userRegEncoder: Encoder[AuthenticationError] =
    Encoder.forProduct1("error")(e => e.msg)
}