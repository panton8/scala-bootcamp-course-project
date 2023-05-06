package com.evolution.service

import cats.data.{NonEmptyChain, OptionT}
import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.domain.Club.{Bournemouth, ManCity}
import com.evolution.domain.Position.Forward
import com.evolution.domain.Role.{Captain, Ordinary}
import com.evolution.domain.Status.Healthy
import com.evolution.domain.errors.{NonAvailableTransfer, PriseMoreThanBudget, SuchTeamDoesNotExist, SuchUserDoesNotExist}
import com.evolution.domain.{Budget, GameWeek, Id, Name, Player, Price, Role, Surname, Team, User}
import com.evolution.repository._

final case class TeamService() {

  def showListOfTeams(): IO[List[Team]] = {
    //TeamRepository.listOfTeams()
    implicit val teamOrdering1: Ordering[Team] = Ordering.fromLessThan(_.points.value < _.points.value)
    //implicit val teamOrdering2: Ordering[Team] = Ordering.fromLessThan(_.points.value > _.points.value)
    TeamRepository.listOfTeams().map(_.sorted(teamOrdering1))
    //TeamRepository.listOfTeams().map(_.sorted(teamOrdering2))
  }

  def createTeam(name: Name, userId: Id, players: List[Player], captain: Player): IO[Unit] = {
    if (players.map(_.price.value).sum > 100)
      IO.raiseError(PriseMoreThanBudget)
    else {
      for {
        teamId <- TeamRepository.createTeam(name, userId)
        _      <- TeamRepository.addPlayers(players.map(_.id), Id(teamId))
        _      <- TeamRepository.setCaptain(captain, Id(teamId))
        user   <- UserRepository.userById(userId)
        _      <- user match {
          case Some(user) => UserRepository.changeBudget(userId, user.budget, players)
          case None       => IO.raiseError(SuchUserDoesNotExist)
        }
      } yield ()
    }
  }

  def resetCaptain(teamId: Id, newCaptain: Player): IO[Unit] = for {
    _ <- TeamRepository.setCaptain(newCaptain, teamId)
    _ <- TeamRepository.setOrdinary(teamId, newCaptain)
  } yield()

  def findById(id: Id): IO[Option[Team]] =
    TeamRepository.teamById(id)

  def findByName(name: Name): IO[Option[Team]] =
    TeamRepository.teamByName(name)

  def changePlayer(newPlayer: Player, subPlayer:Player, teamId: Id): IO[Unit] = {
      for {
        posUserId <- TeamRepository.teamOwner(teamId)
        posUser <- posUserId match {
          case Some(id) => UserRepository.userById(id)
          case None     => IO.raiseError(SuchTeamDoesNotExist)
        }
        user  <- posUser match {
          case Some(user) =>
            if (user.budget.value - (newPlayer.price.value - subPlayer.price.value) < 0)
              IO.raiseError(PriseMoreThanBudget)
            else IO.pure(user)
          case None        => IO.raiseError(SuchUserDoesNotExist)
        }
        posTeam <- TeamRepository.teamById(teamId)
        team <- posTeam match {
          case Some(team) =>
            if (team.freeTransfers.value == 0)
              IO.raiseError(NonAvailableTransfer)
            else
              IO.pure(team)
          case None => IO.raiseError(SuchTeamDoesNotExist)
        }
        _ <- TeamRepository.deletePlayer(subPlayer, team.id)
        _ <- TeamRepository.insertPlayer(newPlayer, team.id)
        role <- TeamRepository.getRole(subPlayer.id, team.id)
        _ <- role match {
          case Some(role) =>
            if (role == Captain)
              resetCaptain(teamId, newPlayer)
            else
              IO.unit
          case None       => IO.unit
        }
        _ <- UserRepository.updateBudget(user.id, user.budget, Budget(newPlayer.price.value - subPlayer.price.value))
        _ <- TeamRepository.makeTransfer(team.id, team.freeTransfers)
      } yield ()
    }

  def updateTeamStat(teamId: Id, currentWeek: GameWeek): IO[Unit] = for {
    updateTeam <- TeamRepository.teamPoints(teamId, currentWeek)
    posTeam <- TeamRepository.teamById(teamId)
    _ <- posTeam match {
      case Some(team) => TeamRepository.updateTeamInfo(teamId, team.points, updateTeam.points, team.freeTransfers)
      case None       => IO.raiseError(SuchTeamDoesNotExist)
    }
  } yield ()

  def playersFromTeam(teamId: Id): IO[List[Player]] =
    TeamRepository.playersFromTeam(teamId)
}