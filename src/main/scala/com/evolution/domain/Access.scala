package com.evolution.domain

sealed trait Access

final case object Base extends Access{override def toString: String = "Base"}
final case object Admin extends Access{override def toString: String = "Admin"}

object Access {
    def parse(access: String): Access = access match {
      case "Base"  => Base
      case "Admin" => Admin
      case _       => ???
  }
}