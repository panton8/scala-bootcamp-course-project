package com.evolution.http

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.Router
import org.http4s.implicits._
import com.evolution.http.routes.{PlayerRoutes, TeamRoutes, UserRoutes}
import com.evolution.service.{PlayerService, TeamService, UserService}
import org.http4s.ember.server.EmberServerBuilder


object AppServer extends IOApp {

  private val userService: UserService = UserService()
  private val teamService: TeamService = TeamService()
  private val playerService: PlayerService = PlayerService()

  val httpRoutes = Router[IO](
    "/" -> UserRoutes.routes(userService),
    "/" -> TeamRoutes.routes(teamService),
    "/" -> PlayerRoutes.routes(playerService)
  ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = {
    EmberServerBuilder.default[IO]
      .withHttpApp(httpRoutes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}