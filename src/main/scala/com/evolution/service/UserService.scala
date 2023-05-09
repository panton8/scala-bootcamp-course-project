package com.evolution.service

import cats.effect.IO
import com.evolution.domain.{Email, Id, Name, Password, Player, User}
import com.evolution.repository._
import com.evolution.domain.errors._

final case class UserService() {

  def registration(userName: Name, email: Email, password: Password):IO[Option[User]] = for {
      possibleName  <- UserRepository.userByName(userName)
      possibleEmail <- UserRepository.userByEmail(email)
      userId        <- (possibleName, possibleEmail) match {
        case (None, None) => UserRepository.addUser(userName, email, password)
        case _            => IO.raiseError(SuchUserAlreadyExist("Such email or userName already exist"))
      }
      user          <- UserRepository.userById(Id(userId))
  } yield  user

  def signIn(email: Email, password: Password): IO[Option[User]] =
    UserRepository.getUser(email, password)
    //searchResult(UserRepository.getUser(email, password))

  def showListOfUsers(): IO[List[User]] =
    UserRepository.listOfUsers()

  def findById(id: Id): IO[Option[User]] =
    UserRepository.userById(id)
    //searchResult(UserRepository.userById(id))

  def findByEmail(email: Email): IO[Option[User]] =
    UserRepository.userByEmail(email)
    //searchResult(UserRepository.userByEmail(email))

  def findByName(userName: Name): IO[Option[User]] =
    UserRepository.userByName(userName)
    //searchResult(UserRepository.userByName(userName))

  def changeBudget(user: User, players: List[Player]): IO[Int] =
    UserRepository.changeBudget(user.id, user.budget, players)

  def deleteTeamWithPlayers(teamId: Id): IO[Unit] = for {
    _ <- TeamRepository.deleteTeamPlayers(teamId)
    _ <- TeamRepository.deleteTeam(teamId)
  } yield ()

  def deleteUser(email: Email): IO[Unit] = for {
    user <- UserRepository.userByEmail(email)
    teamId <- user match {
      case Some(user) => TeamRepository.findByOwner(user.id)
      case None       => IO.raiseError(SuchUserDoesNotExist)
    }
    _ <- teamId match {
      case Some(id) => deleteTeamWithPlayers(id)
      case None     => IO.unit
    }
    _ <- UserRepository.banUser(email)
  } yield ()
}