package com.evolution.http.routes

import cats.effect._
import com.evolution.domain.errors.SuchTeamDoesNotExist
import com.evolution.domain.{GameWeek, Id, Player}
import com.evolution.http.domain.TeamCreation
import com.evolution.service.TeamService
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl._

object TeamRoutes {

  def routes(teamService: TeamService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "teams" =>
        teamService.showListOfTeams().flatMap(teams => Ok(teams))

      case GET -> Root / "teams" / IntVar(id) =>
        teamService.findById(Id(id)) flatMap {
          case Some(team) => Ok(team)
          case None       => NotFound()
        }

      case GET -> Root / "teams" / IntVar(id) / "line-up" =>
        teamService.playersFromTeam(Id(id)) flatMap {players =>
          if (players.isEmpty)
            NotFound(SuchTeamDoesNotExist.getMessage)
          else
            Ok(players)
        }

      case req @ PUT -> Root / "teams" / IntVar(id) / "update" / "captain" =>
        for {
          newCaptain <- req.as[Player]
          response   <- Ok(teamService.resetCaptain(Id(id), newCaptain))
        } yield response

      case PUT -> Root / "teams" / IntVar(id) / "matchweek" / IntVar(currentWeek) / "update" / "points" =>
        teamService.updateTeamStat(Id(id),GameWeek(currentWeek)) flatMap(_ => Ok().handleErrorWith(e => BadRequest(e.getMessage)))

      case req @ PUT -> Root / "teams" / IntVar(teamId) / "update" / "line-up" =>
        for {
          players  <- req.as[List[Player]]
          response <- Ok(teamService.changePlayer(players(1), players.head,Id(teamId))).handleErrorWith(e => BadRequest(e.getMessage))
        } yield response

      case req @ POST -> Root / "users" / IntVar(id) / "teams" =>
        for {
          team     <- req.as[TeamCreation]
          response <- Ok(teamService.createTeam(team.name, Id(id), team.players, team.captain)).handleErrorWith(e => BadRequest(e.getMessage))
        } yield response
    }
  }
}
