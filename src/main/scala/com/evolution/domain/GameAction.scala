package com.evolution.domain

abstract class GameAction{
  val points: Int => Points
}

object Goal extends GameAction {
  override val points = (amount: Int) => Points(6 * amount)
}

object Assist extends GameAction {
  override val points = (amount: Int) => Points(3 * amount)
}

object YellowCard extends GameAction {
  override val points= (amount: Int) => Points(-1 * amount)
}

object RedCard extends GameAction {
  override val points = (amount: Int) => Points(-2 * amount)
}

object OwnGoal extends GameAction {
  override val points = (amount: Int) => Points(-3 * amount)
}

object Saves extends GameAction {
  override val points = (amount: Int) => Points((1 * amount)/3)
}

object Minutes extends GameAction {
  override val points = {
    case value if value >= 60 => Points(2)
    case value if value == 0 => Points(0)
    case _ => Points(1)
  }
}

object CleanSheet extends GameAction {
  override val points = (amount: Int) => Points(6 * amount)
}