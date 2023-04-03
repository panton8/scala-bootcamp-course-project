package com.evolution.domain

trait Position

object Forward extends Position {override def toString: String = "FWD"}
object Midfielder extends Position {override def toString: String = "MID"}
object Defender extends Position {override def toString: String = "DEF"}
object Goalkeeper extends Position {override def toString: String = "GKP"}

object PositionSyntax {
  implicit class GetPosition(val position: String) extends AnyVal {
    def toPos: Position = position match {
      case "FWD" => Forward
      case "MID" => Midfielder
      case "DEF" => Defender
      case "GKP" => Goalkeeper
    }
  }
}
