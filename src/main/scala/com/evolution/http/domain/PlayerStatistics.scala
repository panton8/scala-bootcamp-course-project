package com.evolution.http.domain

import com.evolution.domain.{GameWeek, Id, Statistic}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class PlayerStatistics(
  playerId: Id,
  goals: Int,
  assists: Int,
  minutes: Int,
  ownGoals: Int,
  yellowCard: Int,
  redCard: Int,
  saves: Int,
  cleanSheet: Int,
  gameWeek: GameWeek
)

object PlayerStatistics {
  implicit val jsonDecoder: Decoder[PlayerStatistics] = cursor =>
    for {
      playerId   <- cursor.get[Int]("playerId")
      goals      <- cursor.get[Int]("goals")
      assists    <- cursor.get[Int] ("assists")
      minutes    <- cursor.get[Int] ("minutes")
      ownGoals   <- cursor.get[Int] ("ownGoals")
      yellowCard <- cursor.get[Int] ("yellowCards")
      redCard    <- cursor.get[Int] ("redCards")
      saves      <- cursor.get[Int]("saves")
      cleanSheet <- cursor.get[Int]("cleanSheets")
      gameWeek   <- cursor.get[Int]("gameWeek")
    } yield PlayerStatistics(Id(playerId), goals, assists, minutes, ownGoals, yellowCard, redCard, saves, cleanSheet, GameWeek(gameWeek))

  implicit val jsonEncoder: Encoder[PlayerStatistics] = Encoder.instance {
    case PlayerStatistics(playerId, goals, assists, minutes, ownGoals, yellowCard, redCard, saves, cleanSheet, gameWeek) => Json.obj(
      "playerId" -> playerId.value.asJson,
      "goals"           -> goals.asJson,
      "assists"         -> assists.asJson,
      "minutes"         -> minutes.asJson,
      "ownGoals"        -> ownGoals.asJson,
      "yellowCards"     -> yellowCard.asJson,
      "redCards"        -> redCard.asJson,
      "saves"           -> saves.asJson,
      "cleanSheets"     -> cleanSheet.asJson,
      "gameWeek"        -> gameWeek.value.asJson
    )
  }
}