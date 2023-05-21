package com.evolution.http.domain

import com.evolution.domain.{Id, Name}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class TeamCreation(name: Name, players: List[Id], captain: Id)

object TeamCreation {
  implicit val jsonDecoder: Decoder[TeamCreation] = cursor =>
  for {
    name    <- cursor.get[String]("name")
    players <- cursor.get[List[Id]]("players")
    captain <- cursor.get[Id]("captain")
  }  yield TeamCreation(Name(name), players, captain)

  implicit val jsonEncoder: Encoder[TeamCreation] = Encoder.instance {
    case TeamCreation(name, players, captain) => Json.obj(
      "name" -> name.value.asJson,
      "players"     -> players.asJson,
      "captain"     -> captain.asJson
    )
  }
}
