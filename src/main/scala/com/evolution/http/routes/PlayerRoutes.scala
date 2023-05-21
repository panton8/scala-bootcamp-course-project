package com.evolution.http.routes

import cats.effect._
import com.evolution.domain.Access.{Admin, Base}
import com.evolution.domain.errors.SuchPlayerDoesNotExist
import com.evolution.domain.{Club, GameWeek, Id, Name, Position, Statistic, Surname, User}
import com.evolution.http.auth.Auth.authMiddleware
import com.evolution.http.domain.PlayerStatistics
import com.evolution.service.PlayerService
import org.http4s.{AuthedRoutes, _}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.io._

final case class PlayerRoutes(playerService: PlayerService) {

  def authedRoutes: AuthedRoutes[User, IO] = {
    AuthedRoutes.of {
      case PUT -> Root / IntVar(id) / "health" / "injured" as user =>
        user.access match {
          case Base  => IO(Response(Forbidden))
          case Admin => playerService.getInjured(Id(id)).flatMap(_ => Ok())
        }

      case PUT -> Root / IntVar(id) / "health" / "recovered" as user =>
        user.access match {
          case Base  => IO(Response(Forbidden))
          case Admin => playerService.getRecovered(Id(id)).flatMap(_ => Ok())
        }

      case req@POST -> Root / "statistics" / "matchweek" as user =>
        user.access match {
          case Base  => IO(Response(Forbidden))
          case Admin => for {
            playerStat <- req.req.as[PlayerStatistics]
            plStat <- playerService.addMatchActions(
              playerStat.playerId,
              Statistic(
                playerStat.goals,
                playerStat.assists,
                playerStat.minutes,
                playerStat.ownGoals,
                playerStat.yellowCard,
                playerStat.redCard,
                playerStat.saves,
                playerStat.cleanSheet
              ),
              playerStat.gameWeek
            )
            response <- plStat match {
              case Some(_) => Ok()
              case None => BadRequest()
            }
          } yield response
        }

      case GET -> Root  as user =>
        playerService.showListOfPlayers().flatMap(players => Ok(players).handleErrorWith(e => BadRequest(e.getMessage)))

      case GET -> Root / IntVar(id) as user =>
        playerService.findById(Id(id)) flatMap {
          case Some(player) => Ok(player)
          case None => NotFound()
        }

      case GET -> Root / "club" / club as user =>
        if (Club.values.toList.map(_.entryName).contains(club))
          playerService.showListOfPlayersByClub(Club.withName(club)).flatMap(players => Ok(players))
        else
          BadRequest("Incorrect football club")

      case GET -> Root / "position" / position as user =>
        position match {
          case "FWD" | "MID" | "DEF" | "GKP" => playerService.showListOfPlayersByPosition(Position.withName(position)).flatMap(players => Ok(players))
          case _                             => BadRequest("Incorrect player position")
        }

      case GET -> Root / name / surname as user =>
        playerService.findByName(Name(name), Surname(surname)).flatMap {
          case Some(player) => Ok(player)
          case None => BadRequest("No such player")
        }

      case GET -> Root / IntVar(id) / "statistics" / "total" as user =>
        playerService.takeTotalStatistic(Id(id)).flatMap{
          case Some(statistics) => Ok(statistics)
          case None             => BadRequest(SuchPlayerDoesNotExist.getMessage)
        }
      case GET -> Root / IntVar(id) / "statistics" / "week" / IntVar(week) as user =>
        playerService.takeWeekStatistic(Id(id), GameWeek(week)).flatMap{
          case Some(statistics) => Ok(statistics)
          case None             => BadRequest("Invalid player or week")
        }

      case GET -> Root / IntVar(id) / "points" / "total" as user =>
        playerService.giveTotalPoints(Id(id)).flatMap(points =>
          Ok(points)).handleErrorWith(e => BadRequest(e.getMessage))

      case GET -> Root / IntVar(id) / "points" / "week" / IntVar(week) as user =>
        playerService.givePointsByWeek(Id(id), GameWeek(week)).flatMap(points =>
          Ok(points)).handleErrorWith(e => BadRequest(e.getMessage))
    }
  }

  val routes: HttpRoutes[IO] = authMiddleware(authedRoutes)
}
