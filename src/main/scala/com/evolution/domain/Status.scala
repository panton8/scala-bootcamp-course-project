package com.evolution.domain

sealed trait Status

final case object Healthy extends Status {override def toString: String = "Healthy"}
final case object Injured extends Status {override def toString: String = "Injured"}

object Status {
    def parse(status: String): Status = status match {
      case "Healthy" => Healthy
      case "Injured" => Injured
      case _         => ???
  }
}
