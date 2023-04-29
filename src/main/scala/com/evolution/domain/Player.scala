package com.evolution.domain

import com.evolution.domain.GamePlace.Starter
import com.evolution.domain.Role.Ordinary
import com.evolution.domain.Status.Healthy
import doobie.util.{Read, Write}
import io.circe.{Decoder, Encoder}

final case class Player(
  id: Id,
  name: Name,
  surname: Surname,
  club: Club,
  price: Price,
  position: Position,
  status: Status = Healthy,
  stat: Map[GameWeek, Statistic] = Map()
)

object Player {

  implicit val decodeMap = Decoder.decodeMap[GameWeek, Statistic]
  implicit val encodeMap = Encoder.encodeMap[GameWeek, Statistic]

  implicit val playerDecoder: Decoder[Player] =
    Decoder.forProduct8(
      "id",
      "name",
      "surname",
      "club",
      "price",
      "position",
      "status",
      "stat"
    )(Player.apply)

  implicit val playerEncoder: Encoder[Player] =
    Encoder.forProduct8(
      "id",
      "name",
      "surname",
      "club",
      "price",
      "position",
      "status",
      "stat"
    )(player =>(
      player.id.value,
      player.name.value,
      player.surname.value,
      player.club.entryName,
      player.price.value,
      player.position.entryName,
      player.status,
      player.stat
    ))

  implicit val playerRead: Read[Player] = Read[(Int, String, String, Club, Double, Position,Status)].map {
    case (id, name, surname, club, price, position, status) =>
      Player(
        Id(id),
        Name(name),
        Surname(surname),
        club,
        Price(price),
        position,
        status,
      )
  }

  implicit val playerWrite: Write[Player] = Write[(Int, String, String, Club, Double, Position,Status)].
    contramap { player =>
    (
      player.id.value,
      player.name.value,
      player.surname.value,
      player.club,
      player.price.value,
      player.position,
      player.status,
    )
  }
}