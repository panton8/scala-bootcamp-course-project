package com.evolution.domain

import doobie.util.{Read, Write}

final case class Player(
  id: Id,
  name: Name,
  surname: Surname,
  club: Club,
  price: Price,
  position: Position,
  role:Role = Ordinary,
  status: Status = Healthy,
  gamePlace: GamePlace = Starter,
  stat: Map[GameWeek,Statistic] = Map()
)

object Player {
  implicit val teamRead: Read[Player] = Read[(Int, String, String, String, Double, String, String, String, String)].map {
    case (id, name, surname, club, price, position, role, status, place) =>
      Player(
        Id(id),
        Name(name),
        Surname(surname),
        Club.parse(club),
        Price(price),
        Position.parse(position),
        Role.parse(role),
        Status.parse(status),
        Place.parse(status)
      )
  }

  implicit val teamWrite: Write[Player] = Write[(Int, String, String, String, Double, String, String, String, String)].
    contramap { player =>
    (
      player.id.value,
      player.name.value,
      player.surname.value,
      player.club.toString,
      player.price.value,
      player.position.toString,
      player.role.toString,
      player.status.toString,
      player.gamePlace.toString
    )
  }
}