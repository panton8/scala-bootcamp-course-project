package com.evolution.domain

import com.evolution.domain.GamePlace.Starter
import com.evolution.domain.Role.Ordinary
import com.evolution.domain.Status.Healthy
import doobie.util.{Read, Write}

final case class Player(
  id: Id,
  name: Name,
  surname: Surname,
  club: Club,
  price: Price,
  position: Position,
  status: Status = Healthy,
  gamePlace: GamePlace = Starter,
  stat: Map[GameWeek,Statistic] = Map()
)

object Player {
  implicit val teamRead: Read[Player] = Read[(Int, String, String, Club, Double, Position,Status, GamePlace)].map {
    case (id, name, surname, club, price, position, status, place) =>
      Player(
        Id(id),
        Name(name),
        Surname(surname),
        club,
        Price(price),
        position,
        status,
        place
      )
  }

  implicit val teamWrite: Write[Player] = Write[(Int, String, String, Club, Double, Position,Status, GamePlace)].
    contramap { player =>
    (
      player.id.value,
      player.name.value,
      player.surname.value,
      player.club,
      player.price.value,
      player.position,
      player.status,
      player.gamePlace
    )
  }
}