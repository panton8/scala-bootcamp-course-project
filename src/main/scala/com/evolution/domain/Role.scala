package com.evolution.domain
import enumeratum._

sealed trait Role extends EnumEntry

final case object Role extends Enum[Role] with DoobieEnum[Role] {

  final case object Captain extends Role
  final case object Ordinary extends Role

  val values = findValues
}