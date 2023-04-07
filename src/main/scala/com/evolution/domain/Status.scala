package com.evolution.domain
import enumeratum._

sealed trait Status extends EnumEntry

final case object Status extends Enum[Status] with DoobieEnum[Status] {

  final case object Healthy extends Status
  final case object Injured extends Status

  val values = findValues
}