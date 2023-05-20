package com.evolution.repository

import cats.data.NonEmptyList
import cats.effect.IO
import doobie.implicits._
import utils.DriverTransactor.xa
import com.evolution.domain.Status.{Healthy, Injured}
import com.evolution.domain.{Club, GameWeek, Id, Name, Player, Position, Price, Statistic, Status, Surname}
import doobie.Fragments
import doobie.implicits.toSqlInterpolator

object PlayerRepository{

  def listOfPlayers(): IO[List[Player]] =
    fr"""
        SELECT
            *
        FROM
            players
      """
      .query[Player]
      .stream
      .compile
      .toList
      .transact(xa)

  def getAllPlayersId(): IO[List[Id]] =
    fr"""
        SELECT
            id
        FROM
            players
      """
      .query[Id]
      .stream
      .compile
      .toList
      .transact(xa)

  def playerByNameAndSurname(name: Name, surname: Surname): IO[Option[Player]] =
    fr"""
        SELECT
            *
        FROM
            players
        WHERE
            name = ${ name.value }
            AND surname = ${ surname.value }
      """
      .query[Player]
      .option
      .transact(xa)

  def playerById(id: Id): IO[Option[Player]] =
    fr"""
        SELECT
            *
        FROM
            players
        WHERE
            id = ${  id.value  }
      """
      .query[Player]
      .option
      .transact(xa)

  def playersByClub(club: Club): IO[List[Player]] =
    fr"""
        SELECT
            *
        FROM
            players
        WHERE
            club = $club
      """
      .query[Player]
      .stream
      .compile
      .toList
      .transact(xa)

  def playersByPosition(position: Position): IO[List[Player]] =
    fr"""
        SELECT
            *
        FROM
            players
        WHERE
            pos = $position
      """
      .query[Player]
      .stream
      .compile
      .toList
      .transact(xa)

  def getInjured(playerId: Id): IO[Int] =
    fr"""
        UPDATE
            players
        SET
            health_status = ${Injured.entryName}
        WHERE
            id = ${ playerId.value }
      """
      .update
      .run
      .transact(xa)

  def getRecovered(playerId: Id): IO[Int] =
    fr"""
        UPDATE
            players
        SET
            health_status = ${Healthy.entryName}
        WHERE
            id = ${ playerId.value }
      """
      .update
      .run
      .transact(xa)

  def changePrice(playerId: Id, price: Price): IO[Int] =
    fr"""
        UPDATE
            players
        SET
            price = ${ price.value }
        WHERE
            id = ${ playerId.value }
      """
      .update
      .run
      .transact(xa)

  def changeClub(playerId: Id, club: Club): IO[Int] =
    fr"""
        UPDATE
            players
        SET
            club = $club
        WHERE
            id = ${ playerId.value }
      """
      .update
      .run
      .transact(xa)

  def updateStatistics(statistic: Statistic, player: Id, gameWeek: GameWeek): IO[Option[Int]] =
    fr"""
        INSERT INTO
            statistics (
                game_week,
                goals,
                assists,
                minutes,
                own_goals,
                yellow_cards,
                red_cards,
                saves,
                clean_sheet,
                player_id
             )
        VALUES
            (
               ${gameWeek.value},
               ${statistic.goals},
               ${statistic.assists},
               ${statistic.minutes},
               ${statistic.ownGoals},
               ${statistic.yellowCard},
               ${statistic.redCard},
               ${statistic.saves},
               ${statistic.cleanSheet},
               ${player.value}
            )
      """
      .update
      .run
      .transact(xa)
      .option

  def showPlayerStatisticsByWeek(playerId: Id, gameWeek: GameWeek): IO[Option[Statistic]] =
    fr"""
        SELECT
            goals,
            assists,
            minutes,
            own_goals,
            yellow_cards,
            red_cards,
            saves,
            clean_sheet
        FROM
            statistics
        WHERE
            player_id = ${ playerId.value }
            AND game_week = ${ gameWeek.value }
      """
      .query[Statistic]
      .option
      .transact(xa)

  def showTotalPlayerStatistics(playerId: Id):IO[Option[Statistic]] =
    fr"""
        SELECT
            SUM(goals),
            SUM(assists),
            SUM(minutes),
            SUM(own_goals),
            SUM(yellow_cards),
            SUM(red_cards),
            SUM(saves),
            SUM(clean_sheet)
        FROM
            statistics
        WHERE
            player_id = ${ playerId.value }
      """
      .query[Statistic]
      .option
      .transact(xa)

  def addPlayer(name: Name, surname: Surname, price: Price, club: Club, position: Position, status: Status): IO[Int] =
    fr"""
        INSERT INTO
             players (
                 name,
                 surname,
                 club,
                 price,
                 pos,
                 health_status,
                 game_place
             )
    VALUES
        (
            ${ name.value },
            ${ surname.value },
            ${ club.toString },
            ${ price.value },
            ${ position.entryName },
            ${ status }
        )
      """
      .update.withUniqueGeneratedKeys[Int]("id")
      .transact(xa)


  def playersPos(players: NonEmptyList[Id]): IO[List[Position]] = {
    val q =
      fr"""
        SELECT
            pos
        FROM
            players
        WHERE
      """ ++ Fragments.in(fr"id", players.map(id => id.value))
    q.query[Position]
      .stream
      .compile
      .toList
      .transact(xa)
  }

  def playerPos(player: Id): IO[Option[Position]] =
      fr"""
        SELECT
            pos
        FROM
            players
        WHERE
            id = ${player.value}
      """
        .query[Position]
        .option
        .transact(xa)
}
