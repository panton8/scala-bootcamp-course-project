package com.evolution.http.routes

import cats.effect._
import com.evolution.domain.Access.{Admin, Base}
import com.evolution.domain.errors.SuchTeamDoesNotExist
import com.evolution.domain.{GameWeek, Id, Player, User}
import com.evolution.http.auth.Auth.authMiddleware
import com.evolution.http.domain.TeamCreation
import com.evolution.service.TeamService
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

final case class TeamRoutes(teamService: TeamService) {

  private def authedRoutes: AuthedRoutes[User, IO] =
    AuthedRoutes.of {
      case PUT -> Root / "teams" / IntVar(id) / "matchweek" / IntVar(currentWeek) / "update" / "points" as user =>
        user.access match {
          case Base  => IO(Response(Forbidden))
          case Admin => teamService.updateTeamStat(Id(id), GameWeek(currentWeek)) flatMap (_ =>
            Ok())
        }

      case GET -> Root / "teams" as user =>
        teamService.showListOfTeams().flatMap(teams => Ok(teams))

      case GET -> Root / "teams" / IntVar(id) as user =>
        teamService.findById(Id(id)) flatMap {
          case Some(team) => Ok(team)
          case None => NotFound()
        }

      case GET -> Root / "teams" / IntVar(id) / "line-up" as user =>
        teamService.playersFromTeam(Id(id)) flatMap { players =>
          if (players.isEmpty)
            NotFound(SuchTeamDoesNotExist.getMessage)
          else
            Ok(players)
        }

      case req@PUT -> Root / "teams" / IntVar(id) / "update" / "captain" as user =>
        for {
          newCaptain <- req.req.as[Player]
          response <- Ok(teamService.resetCaptain(Id(id), newCaptain))
        } yield response

      case req@PUT -> Root / "teams" / IntVar(teamId) / "update" / "line-up" as user =>
        for {
          players <- req.req.as[List[Player]]
          response <- Ok(teamService.changePlayer(players(1), players.head, Id(teamId))).handleErrorWith(e => BadRequest(e.getMessage))
        } yield response

      case req@POST -> Root / "users" / IntVar(id) / "teams" as user=>
        for {
          team     <- req.req.as[TeamCreation]
          response <- Created(teamService.createTeam(team.name, Id(id), team.players, team.captain)).handleErrorWith(e => BadRequest(e.getMessage))
        } yield response
    }

  val routes: HttpRoutes[IO] = authMiddleware(authedRoutes)
}
