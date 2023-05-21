package com.evolution.domain

import io.circe.{Decoder, Encoder, HCursor, KeyDecoder, KeyEncoder}

final case class GameWeek(value: Int) extends AnyVal

object GameWeek {

  implicit val gameWeekDecoder: Decoder[GameWeek] =
    Decoder.forProduct1("gameWeek")(GameWeek.apply)

  implicit val gameWeekEncoder: Encoder[GameWeek] =
    Encoder.forProduct1("gameWeek")(week => week.value)

  implicit val gameWeekKeyDecoder: KeyDecoder[GameWeek] = (s: String) => {
    Some(GameWeek(s.toInt))
  }

  implicit val gameWeekKeyEncoder: KeyEncoder[GameWeek] =
    (week: GameWeek) => s"${week.value}"
}
