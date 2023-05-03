package com.evolution.domain
import enumeratum._

sealed trait Access extends EnumEntry

final case object Access extends Enum[Access] with DoobieEnum[Access] {

  final case object Base extends Access
  final case object Admin extends Access

  val values = findValues
}

