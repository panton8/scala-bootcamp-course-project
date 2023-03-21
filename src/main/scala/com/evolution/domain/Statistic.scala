package com.evolution.domain

final case class Statistic(
  gameWeek: GameWeek,
  yellowCard: Int,
  redCard: Int,
  goalScored:Int,
  ownGoals: Int,
  minPlayed: Int,
  isStarter:Boolean,
  points: Points
)


