package com.evolution.domain

import com.evolution.domain.Role.{Captain, Ordinary}
import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder, Json}

final case class Statistic(
  goals:Int,
  assists: Int,
  minutes: Int,
  ownGoals: Int,
  yellowCard: Int,
  redCard: Int,
  saves: Int,
  cleanSheet: Int
)

object Statistic {

  implicit val jsonDecoder: Decoder[Statistic] = cursor =>
    for {
      goals      <- cursor.get[Int]("goals")
      assists    <- cursor.get[Int]("assists")
      minutes    <- cursor.get[Int]("minutes")
      ownGoals   <- cursor.get[Int]("ownGoals")
      yellowCard <- cursor.get[Int]("yellowCards")
      redCard    <- cursor.get[Int]("redCards")
      saves      <- cursor.get[Int]("saves")
      cleanSheet <- cursor.get[Int]("cleanSheets")

    } yield Statistic(goals, assists, minutes, ownGoals, yellowCard, redCard, saves, cleanSheet)

  implicit val jsonEncoder: Encoder[Statistic] = Encoder.instance {
    case Statistic(goals, assists, minutes, ownGoals, yellowCard, redCard, saves, cleanSheet) => Json.obj(
      "goals" -> goals.asJson,
      "assists"      -> assists.asJson,
      "minutes"      -> minutes.asJson,
      "ownGoals"     -> ownGoals.asJson,
      "yellowCards"  -> yellowCard.asJson,
      "redCards"     -> redCard.asJson,
      "saves"        -> saves.asJson,
      "cleanSheets"  -> cleanSheet.asJson
    )
  }

  def countPoints(statistic: Statistic, position: Position, role: Role = Ordinary) = {
    val points = Goal.points(statistic.goals, position).value +
      Assist.points(statistic.assists, position).value +
      Minutes.points(statistic.minutes, position).value +
      YellowCard.points(statistic.yellowCard, position).value +
      RedCard.points(statistic.redCard, position).value +
      CleanSheet.points(statistic.cleanSheet, position).value +
      OwnGoal.points(statistic.ownGoals, position).value +
      Saves.points(statistic.saves, position).value
    if (role == Captain) points * 2 else points
  }
}