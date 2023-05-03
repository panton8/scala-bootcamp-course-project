package com.evolution.domain

import com.evolution.domain.Status.Healthy
import doobie.util.{Read, Write}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Player(
  id: Id,
  name: Name,
  surname: Surname,
  club: Club,
  price: Price,
  position: Position,
  status: Status = Healthy,
)

object Player {

  implicit val jsonDecoder: Decoder[Player] = cursor =>
    for {
      id       <- cursor.get[Int]("id")
      name     <- cursor.get[String]("name")
      surname  <- cursor.get[String]("surname")
      club     <- cursor.get[String]("club")
      price    <- cursor.get[Double]("price")
      position <- cursor.get[String]("position")
      status   <- cursor.get[String]("status")
    } yield Player(
      Id(id),
      Name(name),
      Surname(surname),
      Club.withName(club),
      Price(price),
      Position.withName(position),
      Status.withName(status)
    )

  implicit val jsonEncoder: Encoder[Player] = Encoder.instance {
    case Player(id, name, surname, club, price, position, status) => Json.obj(
      "id" -> id.value.asJson,
      "name"      -> name.value.asJson,
      "surname"   -> surname.value.asJson,
      "club"      -> club.entryName.asJson,
      "price"     -> price.value.asJson,
      "position"  -> position.entryName.asJson,
      "status"    -> status.entryName.asJson
    )
  }

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