package com.evolution.domain

sealed trait Role

final case object Captain extends Role {override def toString: String = "Captain"}
final case object Ordinary extends Role {override def toString: String = "Ordinary"}

object Role {
  def parse(role: String): Role = role match {
    case "Captain"  => Captain
    case "Ordinary" => Ordinary
    case _          => ???
  }
}

