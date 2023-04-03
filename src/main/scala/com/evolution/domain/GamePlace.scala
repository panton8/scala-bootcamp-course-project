package com.evolution.domain

trait GamePlace

object Starter extends GamePlace {override def toString: String = "Starter"}
object Substituter extends GamePlace {override def toString: String = "Substituter"}

object PlaceSyntax {
  implicit class GetPlace(val place: String) extends AnyVal {
    def toPlace: GamePlace = place match {
      case "Starter"     => Starter
      case "Substituter" => Substituter
    }
  }
}
