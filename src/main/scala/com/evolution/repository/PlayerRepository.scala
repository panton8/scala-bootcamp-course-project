package com.evolution.repository

import cats.effect.{ExitCode, IO, IOApp}
import doobie.implicits._
import UserRepository.{Debugger, xa}
import com.evolution.domain.{Arsenal, Chelsea, Club, Defender, GamePlace, Goalkeeper, Healthy, Id, Midfielder, Name, Ordinary, Player, Position, Price, Role, Starter, Status, Substituter, Surname}
import doobie.implicits.toSqlInterpolator

object PlayerRepository extends IOApp {

  def listOfPlayers(): IO[List[Player]] = {
    val allUsers: fs2.Stream[doobie.ConnectionIO, Player] =
      fr"select * from players".query[Player].stream
    allUsers.compile.toList.transact(xa)
  }

  def playerByNameAndSurname(name: Name, surname: Surname): IO[Option[Player]] = {
    fr"select * from players where name=${name.value} and surname=${surname.value}".query[Player].option.transact(xa)
  }

  def playerById(id: Id): IO[Option[Player]] = {
    fr"select * from players where id=${id.value}".query[Player].option.transact(xa)
  }

  def playersByClub(club: Club): IO[List[Player]] = {
    fr"select * from players where club=${club.toString}".query[Player].stream.compile.toList.transact(xa)
  }

  def playersByPosition(position: Position): IO[List[Player]] = {
    fr"select * from players where pos=${position.toString}".query[Player].stream.compile.toList.transact(xa)
  }

  def getInjured(name: Name, surname: Surname): IO[Int] = fr"update players set health_status = ${"Injured"} where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def getRecovered(name: Name, surname: Surname): IO[Int] = fr"update players set health_status = ${"Healthy"} where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def changePrice(name: Name, surname: Surname, price: Price): IO[Int] = fr"update players set price=${price.value} where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def changeClub(name: Name, surname: Surname, club: Club): IO[Int] = fr"update players set club=${club.toString} where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def changeGamePlace(name: Name, surname: Surname): IO[Int] = fr"update players set game_place = case when game_place=${"Starter"} then ${"Substituter"} else ${"Starter"} end where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def setCaptain(name: Name, surname: Surname): IO[Int] = fr"update players set role=${"Captain"} where name=${name.value} and surname=${surname.value}".update.run.transact(xa)

  def addPlayer(name: Name, surname: Surname, price: Price, club: Club, position: Position, role: Role, status: Status, gamePlace: GamePlace):IO[Int] = {
    fr"insert into players (name, surname, club, price, pos, role, health_status, game_place) values (${name.value}, ${surname.value}, ${club.toString},${price.value}, ${position.toString}, ${role.toString}, ${status.toString}, ${gamePlace.toString} )".
        update.withUniqueGeneratedKeys[Int]("id").transact(xa)
  }


  override def run(args: List[String]): IO[ExitCode] = {
    playersByPosition(Defender).debug.as(ExitCode.Success)
    //addPlayer(Name("Andrew"), Surname("Sivy"), Price(0.1), AA, Goalkeeper, Ordinary, Healthy, Substituter).debug.as(ExitCode.Success)
    //addUser(Name("jexommo_12"), Email("jeiniproxommo7458@yopmail.com"), Password("01042023"), Base, Budget(100)).debug.as(ExitCode.Success)
  }
}
