package com.evolution.domain

import com.evolution.domain.Position.{Defender, Forward, Goalkeeper, Midfielder}

abstract class GameAction{
  val points: (Int, Position) => Points
}

object Goal extends GameAction {
  override val points = (amount: Int, position: Position) =>
    position match {
      case Forward                => Points(4 * amount)
      case Midfielder             => Points(5 * amount)
      case Defender | Goalkeeper  => Points(6 * amount)
    }
}

object Assist extends GameAction {
  override val points = (amount: Int, position: Position) => Points(3 * amount)
}

object YellowCard extends GameAction {
  override val points= (amount: Int, position: Position) => Points(-1 * amount)
}

object RedCard extends GameAction {
  override val points = (amount: Int , position: Position) => Points(-3 * amount)
}

object OwnGoal extends GameAction {
  override val points = (amount: Int, position: Position) => Points(-2 * amount)
}

object Saves extends GameAction {
  override val points = (amount: Int, position: Position) => Points((1 * amount)/3)
}

object Minutes extends GameAction {
  override val points = (amount: Int, position: Position) => amount match {
    case value if value == 0  => Points(0)
    case value if value >= 60 => Points(2)
    case _                    => Points(1)
  }
}

object CleanSheet extends GameAction {
  override val points = (amount: Int, position: Position) => position match {
    case Forward               => Points(0 * amount)
    case Midfielder            => Points(1 * amount)
    case Defender | Goalkeeper => Points(4 * amount)
  }
}