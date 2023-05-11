package com.evolution.http

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.Router
import org.http4s.implicits._
import com.evolution.http.routes.{PlayerRoutes, TeamRoutes, UserRoutes}

import org.http4s.ember.server.EmberServerBuilder


object AppServer extends IOApp {

  private val httpRoutes = Router[IO](
    "/" -> UserRoutes.service,
    "/" -> TeamRoutes.service,
    "/" -> PlayerRoutes.service
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder.default[IO]
      .withHttpApp(httpRoutes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}