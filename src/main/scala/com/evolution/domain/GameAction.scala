package com.evolution.domain

abstract class GameAction {
  val points: Points
}

object Goal extends GameAction{
  override val points: Points = Points(6)
}

object Assist extends GameAction{
  override val points: Points = Points(3)
}

object YellowCard extends GameAction{
  override val points: Points = Points(-1)
}

object RedCard extends GameAction{
  override val points: Points = Points(-2)
}

object OwnGoal extends GameAction{
  override val points: Points = Points(-3)
}

object Saves extends GameAction{
  override val points: Points = Points(1)
}