package com.evolution.service

import cats.effect.IO
import com.evolution.domain.errors.{NonAvailableTransfer, PriseMoreThanBudget}
import com.evolution.domain.{Budget, GameWeek, Id, Name, Player, Team, User}
import com.evolution.repository._

final case class TeamService() {

  def showListOfTeams(): IO[List[Team]] = {
    TeamRepository.listOfTeams()
    implicit val teamOrdering1: Ordering[Team] = Ordering.fromLessThan(_.points.value < _.points.value)
    implicit val teamOrdering2: Ordering[Team] = Ordering.fromLessThan(_.points.value > _.points.value)
    TeamRepository.listOfTeams().map(_.sorted(teamOrdering1))
    TeamRepository.listOfTeams().map(_.sorted(teamOrdering2))
  }

  def createTeam(name: Name, user: User, players: List[Player], captain: Player): IO[Unit] = {
    if (players.map(_.price.value).sum > 100)
      IO.raiseError(PriseMoreThanBudget)
    else {
      for {
        teamId <- TeamRepository.createTeam(name, user.id)
        _      <- TeamRepository.addPlayers(players.map(_.id), Id(teamId))
        _      <- TeamRepository.setCaptain(captain, Id(teamId))
        _      <- UserRepository.changeBudget(user.id, user.budget, players)
      } yield ()
    }
  }

  def setCaptain(teamId: Id, newCaptain: Player): IO[Unit] = for {
    _ <- TeamRepository.setCaptain(newCaptain, teamId)
    _ <- TeamRepository.setOrdinary(teamId, newCaptain)
  } yield()

  def findById(id: Id): IO[Option[Team]] =
    TeamRepository.teamById(id)

  def findByName(name: Name): IO[Option[Team]] =
    TeamRepository.teamByName(name)

  def changePlayer(newPlayer: Player, subPlayer:Player, team: Team, user: User): IO[Unit] = {
    if (user.budget.value - (newPlayer.price.value - subPlayer.price.value) < 0)
      IO.raiseError(PriseMoreThanBudget)
    else if (team.freeTransfers.value == 0)
      IO.raiseError(NonAvailableTransfer)
    else{
      for {
        _ <- TeamRepository.deletePlayer(subPlayer, team.id)
        _ <- TeamRepository.insertPlayer(newPlayer, team.id)
        _ <- UserRepository.updateBudget(user.id, user.budget, Budget(newPlayer.price.value - subPlayer.price.value))
        _ <- TeamRepository.makeTransfer(team.id, team.freeTransfers)
      } yield ()
    }
  }

  def updateTeamStat(team: Team, currentWeek: GameWeek): IO[Unit] = for {
    updateTeam <- TeamRepository.teamPoints(team.id, currentWeek)
    _          <- TeamRepository.updateTeamInfo(team.id, team.points, updateTeam.points, team.freeTransfers)
  } yield ()
}