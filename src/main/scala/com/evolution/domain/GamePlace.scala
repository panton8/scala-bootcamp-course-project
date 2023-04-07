package com.evolution.domain
import enumeratum._

sealed trait GamePlace extends EnumEntry

final case object GamePlace extends Enum[GamePlace] with DoobieEnum[GamePlace] {

  final case object Starter extends GamePlace
  final case object Substituter extends GamePlace

  val values = findValues
}