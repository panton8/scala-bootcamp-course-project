package com.evolution.service

import cats.data.Validated
import cats.effect.IO
import com.evolution.domain.{Email, Id, Name, Password, User}
import com.evolution.repository._
import com.evolution.domain.errors.{RegErrors, SpecificError, SuchUserAlreadyExist, SuchUserDoesNotExist}
import com.evolution.service.domain.RegistrationValidator
import com.roundeights.hasher.Implicits._
import scala.language.postfixOps

final case class UserService() {

  def registration(userName: Name, email: Email, password: Password): IO[Unit] = for {
      validation   <- RegistrationValidator.validate(userName.value, email.value, password.value)
      _            <- validation match {
        case Validated.Valid(entity)   => IO(entity)
        case Validated.Invalid(errors) => IO.raiseError(RegErrors(errors.toString))
      }
      possibleName  <- UserRepository.userByName(userName)
      possibleEmail <- UserRepository.userByEmail(email)
      _             <- (possibleName, possibleEmail) match {
        case (None, None) => UserRepository.addUser(userName, email, Password(password.value.md5.hex))
        case _            => IO.raiseError(SuchUserAlreadyExist)
      }
  } yield()

  def signIn(email: Email, password: Password): IO[Option[User]] =
    UserRepository.getUser(email, Password(password.value.md5.hex))

  def showListOfUsers(): IO[List[User]] =
    UserRepository.listOfUsers()

  def findById(id: Id): IO[Option[User]] =
    UserRepository.userById(id)

  def findByEmail(email: Email): IO[Option[User]] =
    UserRepository.userByEmail(email)

  def findByName(userName: Name): IO[Option[User]] =
    UserRepository.userByName(userName)

  def deleteTeamWithPlayers(teamId: Id): IO[Unit] = for {
    _ <- TeamRepository.deleteTeamPlayers(teamId)
    _ <- TeamRepository.deleteTeam(teamId)
  } yield ()

  def deleteUser(email: Email): IO[Unit] = for {
    _      <- if (email.value == "fpladmin@gmail.com")
                IO.raiseError(SpecificError("It's impossible to delete admin account "))
              else
                IO.unit
    user   <- UserRepository.userByEmail(email)
    teamId <- user match {
      case Some(user) => TeamRepository.findByOwner(user.id)
      case None       => IO.raiseError(SuchUserDoesNotExist)
    }
    _      <- teamId match {
      case Some(id) => deleteTeamWithPlayers(id)
      case None     => IO.unit
    }
    _      <- UserRepository.banUser(email)
  } yield ()
}