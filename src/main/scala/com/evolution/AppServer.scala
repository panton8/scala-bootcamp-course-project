package com.evolution

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.http.routes.{PlayerRoutes, TeamRoutes, UserRoutes}
import com.evolution.service.{PlayerService, TeamService, UserService}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object AppServer extends IOApp {

  private val userService = UserService()
  private val teamService = TeamService()
  private val playerService = PlayerService()

  private val httpRoutes = Router[IO](
    "users/" -> UserRoutes(userService).routes,
    "teams/" -> TeamRoutes(teamService).routes,
    "players/" -> PlayerRoutes(playerService).routes
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder.default[IO]
      .withHttpApp(httpRoutes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}