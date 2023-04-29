package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Budget(value: Double) extends AnyVal

object Budget {
  implicit val decodeBudget: Decoder[Budget] =
    Decoder.forProduct1("budget")(Budget.apply)

  implicit val encodeId: Encoder[Budget] =
    Encoder.forProduct1("budget")(budget => budget.value)
}