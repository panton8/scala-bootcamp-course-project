package com.evolution.domain

final case class Statistic(
  gameWeek: GameWeek,
  goals:Int,
  assists: Int,
  minutes: Int,
  ownGoals: Int,
  yellowCard: Int,
  redCard: Int,
  saves: Int,
  cleanSheet: Boolean
)


