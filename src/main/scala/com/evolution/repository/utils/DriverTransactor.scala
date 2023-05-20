package com.evolution.repository.utils

import cats.effect.IO
import doobie.util.transactor.Transactor

object DriverTransactor {

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5438/",
    "postgres",
    "postgres"
  )
}
