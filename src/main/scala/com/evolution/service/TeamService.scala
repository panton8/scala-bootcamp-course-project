package com.evolution.service

import cats.data.NonEmptyList
import cats.effect.IO
import com.evolution.domain.GamePlace.{Starter, Substituter}
import com.evolution.domain.Position.{Defender, Forward, Goalkeeper, Midfielder}
import com.evolution.domain.Role.Captain
import com.evolution.domain.errors.{DuplicatePlayers, DuplicateTeam, InvalidAmountOfDefenders, InvalidAmountOfForwards, InvalidAmountOfGoalkeepers, InvalidAmountOfMidfielders, InvalidCaptain, InvalidChange, InvalidTeamPlayer, InvalidTransfer, NonAvailableTransfer, NotEnoughPlayers, PriseMoreThanBudget, SpecificError, SuchPlayerDoesNotExist, SuchTeamDoesNotExist, SuchUserDoesNotExist}
import com.evolution.domain.{Budget, GameWeek, Id, Name, Player, Team}
import com.evolution.repository._

final case class TeamService() {

  def showListOfTeams(): IO[List[Team]] = {
    //TeamRepository.listOfTeams()
    //implicit val teamOrdering1: Ordering[Team] = Ordering.fromLessThan(_.points.value < _.points.value)
    implicit val teamOrdering2: Ordering[Team] = Ordering.fromLessThan(_.points.value > _.points.value)
    TeamRepository.listOfTeams().map(_.sorted(teamOrdering2))
    //TeamRepository.listOfTeams().map(_.sorted(teamOrdering2))
  }
  private def playersPosCheck(players: List[Id]): IO[Unit] = for {
    positions <- PlayerRepository.playersPos(NonEmptyList.fromList(players).get)
    _         <- if (positions.filter(pos => pos == Goalkeeper).length != 2) IO.raiseError(InvalidAmountOfGoalkeepers)
                 else if (positions.filter(pos => pos == Defender).length != 5) IO.raiseError(InvalidAmountOfDefenders)
                 else if (positions.filter(pos => pos == Midfielder).length != 5) IO.raiseError(InvalidAmountOfMidfielders)
                 else if (positions.filter(pos => pos == Forward).length != 3) IO.raiseError(InvalidAmountOfForwards)
                 else IO.unit
  } yield ()

  def createTeam(name: Name, userId: Id, players: List[Id], captain: Id): IO[Unit] = {
    if (players.length != 15)
      IO.raiseError(NotEnoughPlayers)
    else if (players.toSet.size != 15)
      IO.raiseError(DuplicatePlayers)
    else if (!players.contains(captain))
      IO.raiseError(InvalidCaptain)
    else {
      for {
        teamExist  <- TeamRepository.findByOwner(userId)
        _          <- teamExist match {
          case Some(_) => IO.raiseError(DuplicateTeam)
          case None    => IO.unit
        }
        _          <- playersPosCheck(players)
        allPlayers <- PlayerRepository.listOfPlayers().map(players => players.map(player => player.id))
        _          <- if (players.forall(allPlayers.contains)) IO.unit else IO.raiseError(SuchPlayerDoesNotExist)
        totalPrice <- TeamRepository.teamCost(NonEmptyList.fromList(players).get)
        _          <- totalPrice match {
          case value if value > 100 => IO.raiseError(PriseMoreThanBudget)
          case _                    => IO.unit
        }
        teamId     <- TeamRepository.createTeam(name, userId)
        _          <- TeamRepository.addPlayers(players, Id(teamId))
        _          <- TeamRepository.setCaptain(captain, Id(teamId))
        user       <- UserRepository.userById(userId)
        _          <- user match {
          case Some(user) => UserRepository.changeBudget(userId, user.budget, totalPrice)
          case None       => IO.raiseError(SuchUserDoesNotExist)
        }
        _          <- TeamRepository.changeGamePlace(players(11), Id(teamId), Substituter)
        _          <- TeamRepository.changeGamePlace(players(12), Id(teamId), Substituter)
        _          <- TeamRepository.changeGamePlace(players(13), Id(teamId), Substituter)
        _          <- TeamRepository.changeGamePlace(players(14), Id(teamId), Substituter)
      } yield ()
    }
  }

  def resetCaptain(teamId: Id, newCaptain: Id): IO[Unit] = for {
    teamPlayers <- TeamRepository.playersFromTeam(teamId).map(players => players.map(player => player.id))
    _           <- if (teamPlayers.contains(newCaptain)) IO.unit else IO.raiseError(InvalidTeamPlayer)
    _           <- TeamRepository.setCaptain(newCaptain, teamId)
    _           <- TeamRepository.setOrdinary(teamId, newCaptain)
  } yield()

  def findById(id: Id): IO[Option[Team]] =
    TeamRepository.teamById(id)

  def findByName(name: Name): IO[Option[Team]] =
    TeamRepository.teamByName(name)

  def changePlayer(newPlayer: Id, subPlayer: Id, teamId: Id): IO[Unit] = {
      for {
        teamPlayers <- TeamRepository.playersFromTeam(teamId).map(players => players.map(player => player.id))
        _           <- if (teamPlayers.contains(subPlayer)) IO.unit else IO.raiseError(InvalidTeamPlayer)
        allPlayers  <- PlayerRepository.listOfPlayers().map(players => players.map(player => player.id))
        _           <- if (allPlayers.contains(newPlayer)) IO.unit else IO.raiseError(SuchPlayerDoesNotExist)
        _           <- if (teamPlayers.contains(newPlayer)) IO.raiseError(InvalidChange) else IO.unit
        newPos      <- PlayerRepository.playerPos(newPlayer)
        subPos      <- PlayerRepository.playerPos(subPlayer)
        _           <- (newPos, subPos) match {
          case (Some(newPos), Some(subPos)) => if (newPos == subPos) IO.unit else IO.raiseError(SpecificError("You need to choose player with the same position"))
          case (_, _)                       => IO.raiseError(SuchPlayerDoesNotExist)
        }
        posUserId   <- TeamRepository.teamOwner(teamId)
        posUser     <- posUserId match {
          case Some(id) => UserRepository.userById(id)
          case None     => IO.raiseError(SuchTeamDoesNotExist)
        }
        posNewPlayer <- PlayerRepository.playerById(newPlayer)
        posSubPlayer <- PlayerRepository.playerById(subPlayer)
        newPlayer    <- posNewPlayer match {
          case Some(player) => IO.pure(player)
          case None         => IO.raiseError(SuchPlayerDoesNotExist)
        }
        subPlayer    <- posSubPlayer match {
          case Some(player) => IO.pure(player)
          case None         => IO.raiseError(SuchPlayerDoesNotExist)
        }
        user         <- posUser match {
          case Some(user) =>
            if (user.budget.value - (newPlayer.price.value - subPlayer.price.value) < 0)
              IO.raiseError(InvalidTransfer)
            else IO.pure(user)
          case None       => IO.raiseError(SuchUserDoesNotExist)
        }
        posTeam     <- TeamRepository.teamById(teamId)
        team        <- posTeam match {
          case Some(team) =>
            if (team.freeTransfers.value == 0)
              IO.raiseError(NonAvailableTransfer)
            else
              IO.pure(team)
          case None       => IO.raiseError(SuchTeamDoesNotExist)
        }
        role        <- TeamRepository.getRole(subPlayer.id, team.id)
        _           <- TeamRepository.deletePlayer(subPlayer.id, team.id)
        _           <- TeamRepository.insertPlayer(newPlayer.id, team.id)
        _           <- role match {
          case Some(role) =>
            if (role == Captain)
              TeamRepository.setCaptain(newPlayer.id, teamId)
            else
              IO.unit
          case None       => IO.unit
        }
        _          <- UserRepository.updateBudget(user.id, user.budget, Budget("%.1f".format(newPlayer.price.value - subPlayer.price.value).replace(",", ".").toDouble))
        _          <- TeamRepository.makeTransfer(team.id, team.freeTransfers)
      } yield ()
    }

  def updateTeamStat(team: Team, currentWeek: GameWeek): IO[Unit] = for {
    updateTeam <- TeamRepository.teamPoints(team.id, currentWeek)
    _          <- TeamRepository.updateTeamInfo(team.id, team.points, updateTeam.points, team.freeTransfers)
  } yield ()

  def makeSubstitution(subPlayer: Id, newPlayer: Id, team: Id): IO[Unit] = for {
    teamPlayers <- TeamRepository.playersFromTeam(team).map(players => players.map(player => player.id))
    -           <- if (teamPlayers.contains(subPlayer) && teamPlayers.contains(newPlayer)) IO.unit else IO.raiseError(InvalidTeamPlayer)
    newRole     <- TeamRepository.getGamePlace(newPlayer, team)
    subRole     <- TeamRepository.getGamePlace(subPlayer, team)
    _           <- newRole match {
      case Some(role) => if (role == Substituter) IO.unit else IO.raiseError(SpecificError("New player already is starter"))
      case None       => IO.raiseError(SpecificError("Invalid players"))
    }
    _           <- subRole match {
      case Some(role) => if (role == Starter) IO.unit else IO.raiseError(SpecificError("Sub player already is substituter"))
      case None       => IO.raiseError(SpecificError("Invalid players"))
    }
    subRole     <- TeamRepository.getRole(subPlayer, team)
    -           <- subRole match {
      case Some(role) => if (role == Captain) resetCaptain(team, newPlayer) else IO.unit
      case None       => IO.raiseError(SpecificError("Smth go wrong. Try again"))
    }
    _           <- TeamRepository.changeGamePlace(subPlayer, team, Substituter)
    _           <- TeamRepository.changeGamePlace(newPlayer, team, Starter)
  } yield ()

  def playersFromTeam(teamId: Id): IO[List[Player]] =
    TeamRepository.playersFromTeam(teamId)

  def teamByOwner(user: Id): IO[Option[Id]] =
    TeamRepository.findByOwner(user)
}