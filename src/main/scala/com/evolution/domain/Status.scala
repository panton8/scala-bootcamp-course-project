package com.evolution.domain

trait Status

object Healthy extends Status {override def toString: String = "Healthy"}
object Injured extends Status {override def toString: String = "Injured"}

object StatusSyntax {
  implicit class GetStatus(val status: String) extends AnyVal {
    def toStatus: Status = status match {
      case "Healthy" => Healthy
      case "Injured" => Injured
    }
  }
}
