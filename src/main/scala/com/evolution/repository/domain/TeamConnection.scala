package com.evolution.repository.domain

import com.evolution.domain.{Club, GamePlace, GameWeek, Id, Name, Points, Position, Price, Role, Status, Surname, Transfer}
import doobie.util.Read

final case class TeamConnection(
  teamId: Id,
  teamName: Name,
  freeTransfers: Transfer,
  playerId: Id,
  playerName: Name,
  playerSurname: Surname,
  club: Club,
  price: Price,
  position: Position,
  status: Status,
  role: Role,
  gamePlace: GamePlace,
  gameWeek: GameWeek,
  goals: Int,
  assists: Int,
  minutes: Int,
  ownGoals: Int,
  yellowCards: Int,
  redCards: Int,
  saves: Int,
  cleanSheet: Int
)

object TeamConnection {
  implicit val teamConnectionRead: Read[TeamConnection] = Read[(Int, String, Int, Int, String, String, Club, Double, Position, Status, Role, GamePlace, Int, Int, Int, Int, Int, Int, Int, Int, Int)].map{
    case (
      teamId,
      teamName,
      transfers,
      playerId,
      playerName,
      playerSurname,
      club,
      price,
      position,
      healthStatus,
      role,
      gamePlace,
      gameWeek,
      goals,
      assists,
      minutes,
      ownGoals,
      yellowCards,
      redCards,
      saves,
      cleanSheet
      ) =>
      TeamConnection(
        Id(teamId),
        Name(teamName),
        Transfer(transfers),
        Id(playerId),
        Name(playerName),
        Surname(playerSurname),
        club,
        Price(price),
        position,
        healthStatus,
        role,
        gamePlace,
        GameWeek(gameWeek),
        goals,
        assists,
        minutes,
        ownGoals,
        yellowCards,
        redCards,
        saves,
        cleanSheet
      )
  }
}