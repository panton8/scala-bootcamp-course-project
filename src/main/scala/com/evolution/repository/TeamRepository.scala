package com.evolution.repository

import cats.effect.{Deferred, ExitCode, IO, IOApp}
import com.evolution.domain.Role.{Captain, Ordinary}
import com.evolution.domain.{Club, GameWeek, Id, Name, Player, Points, Price, Role, Statistic, Surname, Team, Transfer}
import doobie.implicits.toSqlInterpolator
import doobie.implicits._
import com.evolution.repository.UserRepository.xa
import doobie.util.update.Update
import cats.implicits._
import com.evolution.domain.GamePlace.{Starter, Substituter}
import com.evolution.domain.Position.Defender
import com.evolution.domain.Status.Healthy
import com.evolution.repository.domain.TeamConnection
import com.evolution.service.TeamService


object TeamRepository {

  def listOfTeams(): IO[List[Team]] =
    fr"""
        SELECT
            id,
            name,
            points,
            available_transfers
        FROM
            teams
      """
      .query[Team]
      .stream
      .compile
      .toList
      .transact(xa)

  def createTeam(name: Name, userId: Id): IO[Int] =
    fr"""
        INSERT INTO
            teams (name, points, available_transfers, user_id)
        VALUES
            (${name.value}, ${0}, ${2}, ${userId.value})
      """
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .transact(xa)

  def addPlayers(players: List[Id], teamId: Id): IO[Int] = {
    val teamWithPlayers =
      """
        INSERT INTO
            teams_players (player_id, team_id, player_role, player_place)
        VALUES
            (?, ?, ?, ?)
      """
    val tableValues = players.map(id => (id, teamId, Ordinary.entryName, Starter.entryName))
    Update[(Id, Id, String, String)](teamWithPlayers).updateMany(tableValues).transact(xa)
  }

  def deletePlayer(player: Player, teamId: Id): IO[Int] =
    fr"""
        DELETE FROM
            teams_players
        WHERE
            player_id = ${player.id.value}
            AND team_id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def insertPlayer(player: Player, teamId: Id): IO[Int] =
    fr"""
        INSERT INTO
            teams_players (team_id, player_id, player_role, player_place)
        VALUES
            (
                ${teamId.value},
                ${player.id.value},
                ${Ordinary.entryName},
                ${Starter.entryName}
            )
      """
      .update
      .run
      .transact(xa)

  def setCaptain(player: Player, teamId: Id): IO[Int] =
    fr"""
        UPDATE
            teams_players
        SET
            player_role = ${Captain.entryName}
        WHERE
            player_id = ${player.id.value}
            AND team_id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def setOrdinary(teamId: Id, player: Player): IO[Int] =
    fr"""
        UPDATE
            teams_players
        SET
            player_role = ${Ordinary.entryName}
        WHERE
            player_id != ${player.id.value}
            AND team_id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def teamById(id: Id): IO[Option[Team]] =
    fr"""
        SELECT
            id,
            name,
            points,
            available_transfers
        FROM
            teams
        WHERE
            id = ${id.value}
      """
      .query[Team]
      .option.
      transact(xa)

  def teamByName(name: Name): IO[Option[Team]] =
    fr"""
        SELECT
            id,
            name,
            points,
            available_transfers
        FROM
            teams
        WHERE
            name = ${name.value}
      """
      .query[Team]
      .option
      .transact(xa)

  def changeGamePlace(teamId: Id, playerId: Id): IO[Int] =
    fr"""
        UPDATE
            teams_players
        SET
            player_place = CASE
                WHEN game_place = $Starter THEN $Substituter
                ELSE $Starter
            END
        WHERE
            team_id = ${teamId.value}
            AND player_id = ${playerId.value}
      """
      .update
      .run
      .transact(xa)

  def teamPoints(teamId: Id, gameWeek: GameWeek): IO[Team] = {
    fr"""
        SELECT
            t.id AS team_id,
            t.name,
            t.available_transfers,
            p.id AS player_id,
            p.name,
            p.surname,
            p.club,
            p.price,
            p.pos,
            p.health_status,
            tp.player_role,
            tp.player_place,
            s.game_week,
            s.goals,
            s.assists,
            s.minutes,
            s.own_goals,
            s.yellow_cards,
            s.red_cards,
            s.saves,
            s.clean_sheet
        FROM
            teams t
            INNER JOIN teams_players tp ON t.id = tp.team_id
            INNER JOIN players p ON tp.player_id = p.id
            INNER JOIN statistics s ON tp.player_id = s.player_id
        WHERE
            t.id = ${teamId.value}
            AND s.game_week = ${gameWeek.value}
      """
      .query[TeamConnection]
      .to[List]
      .map(
        teamC =>
          Team(
            teamC.head.teamId,
            teamC.head.teamName,
            Points(teamC.map(stat =>
              Statistic.countPoints(
                Statistic(
                  stat.goals,
                  stat.assists,
                  stat.minutes,
                  stat.ownGoals,
                  stat.yellowCards,
                  stat.redCards,
                  stat.saves,
                  stat.cleanSheet),
                stat.role)).sum),
            teamC.head.freeTransfers,
          )
      )
      .transact(xa)
  }

  def makeTransfer(teamId: Id, currTransfers: Transfer): IO[Int] =
    fr"""
        UPDATE
            teams
        SET
            available_transfers = ${currTransfers.value - 1}
        WHERE
            id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def updateTeamInfo(teamId: Id, currPoints: Points, weekPoints: Points, transfers: Transfer): IO[Int] =
    fr"""
        UPDATE
            teams
        SET
            points = ${currPoints.value + weekPoints.value},
            available_transfers = ${if (transfers.value == 2) 2 else transfers.value + 1}
        WHERE
            id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def playersFromTeam(teamId: Id) =
    fr"""
        SELECT
            p.id AS player_id,
            p.name,
            p.surname,
            p.club,
            p.price,
            p.pos,
            p.health_status
        FROM
            teams t
            INNER JOIN teams_players tp ON t.id = tp.team_id
            INNER JOIN players p ON tp.player_id = p.id
        WHERE
            t.id = ${teamId.value}
      """
      .query[Player]
      .stream
      .compile
      .toList
      .transact(xa)

  def getRole(playerId: Id, teamId: Id): IO[Option[Role]] =
    fr"""
        SELECT
            player_role
        FROM
            teams_players
        WHERE
            team_id = ${teamId.value}
            AND player_id = ${playerId.value}
      """
      .query[Role]
      .option
      .transact(xa)

  def teamOwner(teamId: Id): IO[Option[Id]] =
    fr"""
        SELECT
            user_id
        FROM
            teams
        WHERE
            id = ${teamId.value}
      """
      .query[Id]
      .option
      .transact(xa)

  def deleteTeam(teamId: Id) =
    fr"""
        DELETE FROM
            teams
        WHERE
            id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def deleteTeamPlayers(teamId: Id): IO[Int] =
    fr"""
        DELETE FROM
            teams_players
        WHERE
            team_id = ${teamId.value}
      """
      .update
      .run
      .transact(xa)

  def findByOwner(userId: Id): IO[Option[Id]] =
    fr"""
        SELECT
            id
        FROM
            teams
        WHERE
            user_id = ${userId.value}
      """
      .query[Id]
      .option
      .transact(xa)
}