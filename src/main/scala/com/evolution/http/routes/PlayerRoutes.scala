package com.evolution.http.routes

import cats.effect._
import com.evolution.domain.{Club, GameWeek, Id, Name, Position, Statistic, Surname}
import com.evolution.http.domain.PlayerStatistics
import com.evolution.service.PlayerService
import org.http4s._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._

object PlayerRoutes {

  def routes(playerService: PlayerService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "players" =>
        playerService.showListOfPlayers().flatMap(players => Ok(players).handleErrorWith(e => BadRequest(e.getMessage)))

      case GET -> Root / "players" / IntVar(id) =>
        playerService.findById(Id(id)) flatMap {
          case Some(player) => Ok(player)
          case None         => NotFound()
        }

      case GET -> Root / "players" / "club" / club =>
        playerService.showListOfPlayersByClub(Club.withName(club)).flatMap(players => Ok(players))

      case GET -> Root / "players" / "position" / position =>
        playerService.showListOfPlayersByPosition(Position.withName(position)).flatMap(players => Ok(players))

      case GET -> Root / "players" / name / surname =>
        playerService.findByName(Name(name), Surname(surname)).flatMap {
          case Some(player) => Ok(player)
          case None         => BadRequest("No such player")
        }

      case GET -> Root / "players" / IntVar(id) / "statistics" / "total" =>
        playerService.takeTotalStatistic(Id(id)).flatMap(statistics => Ok(statistics))

      case GET -> Root / "players" / IntVar(id) / "statistics" / "week" / IntVar(week) =>
        playerService.takeWeekStatistic(Id(id), GameWeek(week)).flatMap(statistics => Ok(statistics))

      case GET -> Root / "players" / IntVar(id) / "points" / "total" =>
        playerService.giveTotalPoints(Id(id)).flatMap(points => Ok(points))

      case GET -> Root / "players" / IntVar(id) / "points" / "week" / IntVar(week) =>
        playerService.givePointsByWeek(Id(id), GameWeek(week)).flatMap(points => Ok(points))

      case PUT -> Root / "players"/ IntVar(id) / "health" / "injured" =>
          playerService.getInjured(Id(id)).flatMap(upd => Ok())

      case PUT -> Root / "players" / IntVar(id) / "health" / "recovered" =>
        playerService.getRecovered(Id(id)).flatMap(upd => Ok())

      case req @ POST -> Root / "players" / "statistics" / "matchweek" =>
        for {
          playerStat <- req.as[PlayerStatistics]
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
            case None    => BadRequest()
          }
        } yield response
    }
  }
}
