package com.evolution.http.routes

import cats._
import cats.effect._
import cats.implicits._
import com.evolution.domain.{Id, Team}
import com.evolution.service.TeamService
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._

object TeamRoutes {

  def routes(teamService: TeamService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "teams" =>
        teamService.showListOfTeams().flatMap(teams => Ok(teams.asJson))

      case GET -> Root / "teams" / IntVar(id) =>
        teamService.findById(Id(id)) flatMap {
          case None => NotFound()
          case Some(team) => Ok(team.asJson)
        }

      case req @ POST -> Root / "teams" =>
        req.decode[Team] { team =>
          teamService.createTeam(team.name, user = ???, team.players, captain = ???)
        }

      case GET -> Root / "teams" / IntVar(id) =>
        teamService.findById(Id(id)) flatMap {
          case None       => NotFound()
          case Some(team) => Ok(team.asJson)
        }

      case req @ PUT -> Root / "teams" / id =>
        teamService.updateTeamStat(team =  ???, currentWeek =  ???)

      case req @ PUT -> Root / "teams" / id =>
        teamService.changePlayer(newPlayer = ???, subPlayer = ???, team = ???, user = ???)
    }
  }
}
