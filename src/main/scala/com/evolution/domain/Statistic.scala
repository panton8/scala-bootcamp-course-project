package com.evolution.domain

import com.evolution.domain.GamePlace.Starter
import com.evolution.domain.Role.{Captain, Ordinary}
import com.evolution.domain.Status.{Healthy, Injured}
import doobie.util.{Read, Write}
final case class Statistic(
  gameWeek: GameWeek,
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
  def countPoints(statistic: Statistic, healthStatus: Status = Healthy, gamePlace: GamePlace = Starter, role: Role = Ordinary) = {
    val points = Goal.points(statistic.goals).value +
      Assist.points(statistic.assists).value +
      Minutes.points(statistic.minutes).value +
      YellowCard.points(statistic.yellowCard).value +
      RedCard.points(statistic.redCard).value +
      CleanSheet.points(statistic.cleanSheet).value +
      OwnGoal.points(statistic.ownGoals).value +
      Saves.points(statistic.saves).value
    if (healthStatus == Injured) 0 else if (role == Captain && gamePlace == Starter) points * 2 else points

  }
}