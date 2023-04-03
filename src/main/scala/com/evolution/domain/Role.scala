package com.evolution.domain

trait Role

object Captain extends Role {override def toString: String = "Captain"}
object Ordinary extends Role {override def toString: String = "Ordinary"}

object RoleSyntax {
  implicit class GetRole(val role: String) extends AnyVal {
    def toRole: Role = role match {
      case "Captain"  => Captain
      case "Ordinary" => Ordinary
    }
  }
}

