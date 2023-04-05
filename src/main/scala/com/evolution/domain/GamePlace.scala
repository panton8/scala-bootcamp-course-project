package com.evolution.domain

sealed trait GamePlace

final case object Starter extends GamePlace {override def toString: String = "Starter"}
final case object Substituter extends GamePlace {override def toString: String = "Substituter"}

object Place {
    def parse(place: String): GamePlace = place match {
      case "Starter"     => Starter
      case "Substituter" => Substituter
      case _             => ???
  }
}
