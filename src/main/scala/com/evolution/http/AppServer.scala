package com.evolution.http

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.server.blaze._
import cats.implicits._
import com.evolution.http.routes.{PlayerRoutes, TeamRoutes, UserRoutes}
import com.evolution.service.{PlayerService, TeamService, UserService}


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
    import scala.concurrent.ExecutionContext.global
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(httpRoutes)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }

}