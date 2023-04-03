package com.evolution.domain

trait Access

object Base extends Access{override def toString: String = "Base"}
object Admin extends Access{override def toString: String = "Admin"}

object AccessSyntax {
  implicit class GetAccess(val access: String) extends AnyVal {
    def toAccess: Access = access match {
      case "Base"  => Base
      case "Admin" => Admin
    }
  }
}