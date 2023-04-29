package com.evolution.domain
import enumeratum._
import io.circe.{Decoder, Encoder}

sealed trait Access extends EnumEntry

final case object Access extends Enum[Access] with DoobieEnum[Access] {

  final case object Base extends Access
  final case object Admin extends Access

  val values = findValues

  implicit val decodeAccess: Decoder[Access] =
    Decoder.forProduct1("access")(Access.values)

  implicit val encodeAccess: Encoder[Access] =
    Encoder.forProduct1("access")(access => access.entryName)
}

