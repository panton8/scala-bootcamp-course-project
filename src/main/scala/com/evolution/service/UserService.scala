package com.evolution.service

import cats.effect.IO
import com.evolution.domain.{Email, Id, Name, Password, User}
import com.evolution.repository._
import com.evolution.domain.errors._

final case class UserService() {

  def registration(userName: Name, email: Email, password: Password):IO[Option[User]] = for {
      possibleName <- UserRepository.userByName(userName)
      possibleEmail <- UserRepository.userByEmail(email)
      userId <- (possibleName, possibleEmail) match {
        case (None, None) => UserRepository.addUser(userName, email, password)
        case _            => IO.raiseError(SuchUserAlreadyExist("Such email or userName already exist"))
      }
      user <- UserRepository.userById(Id(userId))
  } yield  user

  private def searchResult(io: IO[Option[User]]): IO[User] = for {
    possibleUser <- io
    res <- possibleUser match {
      case Some(user) => IO.pure(user)
      case None => IO.raiseError(SuchUserDoesNotExist)
    }
  } yield res

  def signIn(email: Email, password: Password): IO[User] =
    searchResult(UserRepository.getUser(email, password))

  def showListOfUsers: IO[List[User]] =
    UserRepository.listOfUsers()

  def findById(id: Id): IO[User] =
    searchResult(UserRepository.userById(id))

  def findByEmail(email: Email): IO[User] =
    searchResult(UserRepository.userByEmail(email))

  def findByName(userName: Name): IO[User] =
    searchResult(UserRepository.userByName(userName))
}