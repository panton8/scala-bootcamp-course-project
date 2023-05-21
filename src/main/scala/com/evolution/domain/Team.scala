package com.evolution.domain

import doobie.util.{Read, Write}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Team(
  id: Id,
  name: Name,
  points: Points = Points(0),
  freeTransfers: Transfer = Transfer(2),
)

object Team {

  implicit val jsonDecoder: Decoder[Team] = cursor =>
    for {
      id        <- cursor.get[Int]("id")
      name      <- cursor.get[String]("name")
      points    <- cursor.get[Int]("points")
      transfers <- cursor.get[Int]("freeTransfers")
    } yield Team(Id(id), Name(name), Points(points),Transfer(transfers))

  implicit val jsonEncoder: Encoder[Team] = Encoder.instance {
    case Team(id, name, points, transfers) => Json.obj(
      "id"     -> id.value.asJson,
      "name"          -> name.value.asJson,
      "points"        -> points.value.asJson,
      "freeTransfers" -> transfers.value.asJson,
    )
  }

  implicit val teamRead: Read[Team] = Read[(Int, String, Int, Int)].map {
    case (id, name, points, transfers_amount) => Team(Id(id), Name(name), Points(points), Transfer(transfers_amount))
  }

  implicit val teamWrite: Write[Team] = Write[(Int, String, Int, Int)].contramap { team =>
    (team.id.value, team.name.value, team.points.value, team.freeTransfers.value)
  }
}