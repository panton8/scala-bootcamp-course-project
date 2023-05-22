package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Price(value: Double) extends AnyVal