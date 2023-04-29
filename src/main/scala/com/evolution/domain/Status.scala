package com.evolution.domain
import enumeratum._
import io.circe.{Decoder, Encoder}

sealed trait Status extends EnumEntry

final case object Status extends Enum[Status] with DoobieEnum[Status] {

  final case object Healthy extends Status
  final case object Injured extends Status

  val values = findValues

  implicit val decodeAccess: Decoder[Status] =
    Decoder.forProduct1("status")(Status.values)

  implicit val encodeAccess: Encoder[Status] =
    Encoder.forProduct1("status")(status => status.entryName)
}