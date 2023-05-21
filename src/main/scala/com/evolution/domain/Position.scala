package com.evolution.domain
import enumeratum._
import io.circe.{Decoder, Encoder}

sealed abstract class Position(override val entryName: String) extends EnumEntry

final case object Position extends Enum[Position] with DoobieEnum[Position] {

  final case object Forward extends Position("FWD")
  final case object Midfielder extends Position("MID")
  final case object Defender extends Position("DEF")
  final case object Goalkeeper extends Position("GKP")

  val values = findValues
}
