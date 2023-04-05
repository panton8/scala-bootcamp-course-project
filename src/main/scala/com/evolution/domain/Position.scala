package com.evolution.domain

sealed trait Position

final case object Forward extends Position {override def toString: String = "FWD"}
final case object Midfielder extends Position {override def toString: String = "MID"}
final case object Defender extends Position {override def toString: String = "DEF"}
final case object Goalkeeper extends Position {override def toString: String = "GKP"}

object Position {
  def parse(pos: String): Position = pos match {
      case "FWD" => Forward
      case "MID" => Midfielder
      case "DEF" => Defender
      case "GKP" => Goalkeeper
      case _     => ???
    }
}