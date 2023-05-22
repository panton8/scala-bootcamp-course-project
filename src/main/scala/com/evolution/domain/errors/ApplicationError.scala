package com.evolution.domain.errors

import io.circe.{Decoder, Encoder}

sealed abstract class ApplicationError(msg: String) extends Throwable(msg)

final case object SuchUserAlreadyExist extends ApplicationError("Such email or userName already exist")
final case object SuchUserDoesNotExist extends ApplicationError("Such user doesn't exist")
final case object SuchTeamDoesNotExist extends ApplicationError("Such team doesn't exist")
final case object SuchPlayerDoesNotExist extends ApplicationError("Such player doesn't exist")
final case object PriseMoreThanBudget extends ApplicationError("Players prise more than your budget")
final case object InvalidTransfer extends ApplicationError("Not enough budget for the transfer")
final case object NonAvailableTransfer extends ApplicationError("No available transfers")
final case class AuthenticationError(msg: String) extends ApplicationError(msg)
final case object NotEnoughPlayers extends ApplicationError("Not enough players in your team")
final case object InvalidCaptain extends ApplicationError("Captain need to be from your team")
final case object DuplicatePlayers extends ApplicationError("Team can only consist of unique players")
final case object DuplicateTeam extends ApplicationError("You can have only one team")
final case object InvalidTeamPlayer extends ApplicationError("Such player not from your team")
final case object InvalidChange extends ApplicationError("New player is already in your team")
final case class  InvalidEmail(msg: String) extends ApplicationError(msg)
final case class  InvalidPassword(msg: String) extends ApplicationError(msg)
final case class InvalidUserName(msg: String) extends ApplicationError(msg)
final case class RegErrors(msg: String) extends ApplicationError(msg){
  override def getMessage: String = {
    val updMsg = msg.substring(0, msg.length - 1)
    val errors = updMsg.substring(5).split(", ").map(error => error.substring(error.indexOf(" ") + 1))
    errors.mkString(". ")
  }
}
final case class SpecificError(msg: String) extends ApplicationError(msg)
final case object InvalidAmountOfForwards extends ApplicationError("Amount of forwards must be 3")
final case object InvalidAmountOfMidfielders extends ApplicationError("Amount of midfielders must be 5")
final case object InvalidAmountOfDefenders extends ApplicationError("Amount of defenders must be 5")
final case object InvalidAmountOfGoalkeepers extends ApplicationError("Amount of goalkeepers must be 2")

object AuthenticationError {
  implicit val userRegDecoder: Decoder[AuthenticationError] =
    Decoder.forProduct1("error")(AuthenticationError.apply)

  implicit val userRegEncoder: Encoder[AuthenticationError] =
    Encoder.forProduct1("error")(e => e.msg)
}