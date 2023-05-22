package com.evolution.domain

import io.circe.{Decoder, Encoder}

final case class Transfer(value: Int) extends AnyVal
