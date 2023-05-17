package com.evolution.repository

import cats.effect.IO
import com.evolution.domain.Access.Base
import doobie.util.transactor.Transactor
import doobie.implicits._
import com.evolution.domain.{Access, Budget, Email, Id, Name, Password, Player, User}

 object UserRepository {

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5438/",
    "postgres",
    "postgres"
  )

  def listOfUsers(): IO[List[User]] =
    fr"""
        SELECT
            id,
            user_name,
            email,
            password,
            role,
            budget
        FROM
            users
      """
      .query[User]
      .stream
      .compile
      .toList
      .transact(xa)

  def userByName(name: Name): IO[Option[User]] =
    fr"""
        SELECT
            *
        FROM
            users
        WHERE
            user_name=${name.value}
      """
      .query[User]
      .option
      .transact(xa)

  def userByEmail(email: Email): IO[Option[User]] =
    fr"""
        SELECT
            *
        FROM
            users
        WHERE
            email = ${ email.value }
      """
      .query[User]
      .option
      .transact(xa)

   def userById(id: Id): IO[Option[User]] =
     fr"""
         SELECT
             *
         FROM
             users
         WHERE
             id = ${ id.value }
       """
       .query[User]
       .option
       .transact(xa)

  def addUser(userName: Name, email: Email, password: Password, access: Access = Base): IO[Int] =
      fr"""
        INSERT INTO
            users (user_name, email, password, role, budget)
        VALUES
            (${ userName.value }, ${ email.value }, ${ password.value }, $access, ${ 100 })
      """
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .transact(xa)

  def getUser (email: Email, password: Password): IO[Option[User]] =
    fr"""
        SELECT
            *
        FROM
            users
        WHERE
            email = ${ email.value }
            AND password = ${ password.value }
      """
      .query[User]
      .option
      .transact(xa)

   def changeBudget(userId: Id, userBudget: Budget, totalPrice: Double): IO[Int] = {
     fr"""
        UPDATE
            users
        Set
            budget = ${ userBudget.value - totalPrice }
        WHERE
            id = ${ userId.value }
      """
       .update
       .run
       .transact(xa)
   }

   def updateBudget(userId: Id, userBudget: Budget, diff: Budget): IO[Int] =
     fr"""
        UPDATE
            users
        Set
            budget = ${ userBudget.value - diff.value }
        WHERE
            id = ${ userId.value }
      """
       .update
       .run
       .transact(xa)

   def banUser(email: Email): IO[Int] =
     fr"""
        DELETE FROM
            users
        WHERE
            email = ${email.value}
      """
       .update
       .run
       .transact(xa)
}

