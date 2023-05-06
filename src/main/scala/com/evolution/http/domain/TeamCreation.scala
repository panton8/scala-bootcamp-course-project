package com.evolution.http.domain

import com.evolution.domain.{Name, Player}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class TeamCreation(name: Name, players: List[Player], captain: Player)

object TeamCreation {
  implicit val jsonDecoder: Decoder[TeamCreation] = cursor =>
  for {
    name    <- cursor.get[String]("name")
    players <- cursor.get[List[Player]]("players")
    captain <- cursor.get[Player]("captain")
  }  yield TeamCreation(Name(name), players, captain)

  implicit val jsonEncoder: Encoder[TeamCreation] = Encoder.instance {
    case TeamCreation(name, players, captain) => Json.obj(
      "name" -> name.value.asJson,
      "players"     -> players.asJson,
      "captain"     -> captain.asJson
    )
  }
}
