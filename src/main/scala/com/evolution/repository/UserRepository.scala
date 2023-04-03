package com.evolution.repository

import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import doobie.implicits._
import com.evolution.domain.{Access, Admin, Base, Budget, Email, Id, Name, Password, User}

 object UserRepository extends IOApp {

  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5438/",
    "postgres",
    "postgres"
  )

  def listOfUsers(): IO[List[User]] = {
    val allUsers: fs2.Stream[doobie.ConnectionIO, User] =
      fr"select id, user_name, email, password, role, budget from users".query[User].stream
    allUsers.compile.toList.transact(xa)
  }

  def userByName(name: Name): IO[Option[User]] = {
    fr"select id, user_name, email, password, role, budget from users where user_name=${name.value}".
      query[User].
      option.
      transact(xa)
  }

  def userByEmail(email: Email): IO[Option[User]] = {
    val query = fr"select id, user_name, email, password, role, budget from users where email=${email.value}".query[User]
    val action = query.option
    action.transact(xa)
  }

   def userById(id: Id): IO[Option[User]] = {
     val query = fr"select id, user_name, email, password, role, budget from users where id=$id".query[User]
     val action = query.option
     action.transact(xa)
   }

  def addUser(userName: Name, email: Email, password: Password, access: Access, budget: Budget): IO[Int] = {
    fr"insert into users (user_name, email, password, role, budget) values (${userName.value}, ${email.value},${password.value}, ${if (access == Base) "Base" else "Admin"}, ${budget.value})".
          update.withUniqueGeneratedKeys[Int]("id").transact(xa)
  }

  def getUser (email: Email, password: Password): IO[Option[User]] = {
    val query = fr"select id, user_name, email, password, role, budget from users where email=${email.value} and password=${password.value}".query[User]
    val action = query.option
    action.transact(xa)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    userByName(Name("jexommo_12")).debug.as(ExitCode.Success)
    //addUser(Name("jexommo_12"), Email("jeiniproxommo7458@yopmail.com"), Password("01042023"), Base, Budget(100)).debug.as(ExitCode.Success)
  }


}

