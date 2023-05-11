package com.evolution.http.routes

import cats.effect._
import cats.implicits.toSemigroupKOps
import com.evolution.domain.Access.{Admin, Base}
import com.evolution.domain.{Access, Club, GameWeek, Id, Name, Position, Statistic, Surname, User}
import com.evolution.http.auth.Auth.{authUser, inAuthFailure}
import com.evolution.http.domain.PlayerStatistics
import com.evolution.service.PlayerService
import org.http4s.{AuthedRoutes, _}
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._
import org.http4s.dsl.io._
import org.http4s.server.AuthMiddleware

object PlayerRoutes {

  private val playerRoutes: PlayerService = PlayerService()

  val middleware: AuthMiddleware[IO, User] =
    AuthMiddleware(authUser, inAuthFailure)

  val service: HttpRoutes[IO] = middleware(authedRoutes(playerRoutes)) <+> routes(playerRoutes)

  def authedRoutes(playerService: PlayerService): AuthedRoutes[User, IO] = {
    AuthedRoutes.of {
      case PUT -> Root / "players" / IntVar(id) / "health" / "injured" as user =>
        user.access match {
          case Base  => IO(Response(Unauthorized))
          case Admin => playerService.getInjured(Id(id)).flatMap(_ => Ok())
        }

      case PUT -> Root / "players" / IntVar(id) / "health" / "recovered" as user =>
        user.access match {
          case Base  => IO(Response(Unauthorized))
          case Admin => playerService.getRecovered(Id(id)).flatMap(_ => Ok())
        }

      case req@POST -> Root / "players" / "statistics" / "matchweek" as user =>
        user.access match {
          case Base  => IO(Response(Unauthorized))
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
    }
  }

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
    }
  }
}
