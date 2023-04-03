package com.evolution.repository

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.domain.{Id, Name, Team}
import doobie.implicits.toSqlInterpolator
import doobie.implicits._
import com.evolution.repository.UserRepository.xa
import com.evolution.repository.UserRepository.Debugger

object TeamRepository extends IOApp {

  def listOfTeams(): IO[List[Team]] = {
    fr"select (id, name, points, available_transfers) from teams".query[Team].stream.compile.toList.transact(xa)
  }

  def createTeam(name: Name, userId: Id): IO[Int] = {
    fr"insert into teams (name, points, available_transfers, user_id) values (${name.value},${0}, ${2}, ${userId.value})".
      update.withUniqueGeneratedKeys[Int]("id").transact(xa)
  }

  def teamById(id: Id): IO[Option[Team]] = {
    fr"select (id, name, points, available_transfers) from teams where id=${id.value}".query[Team].option.transact(xa)
  }

  def teamByName(name: Name): IO[Option[Team]] = {
    fr"select (id, name, points, available_transfers) from teams where name=${name.value}".query[Team].option.transact(xa)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    teamById(Id(3)).debug.as(ExitCode.Success)
  }
}
