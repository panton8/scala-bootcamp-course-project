package com.evolution.http.routes

import cats._
import cats.effect._
import cats.implicits._
import com.evolution.domain.{Id, Player}
import com.evolution.service.PlayerService
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._

object PlayerRoutes {

  def routes(playerService: PlayerService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._
    HttpRoutes.of[IO] {
      case GET -> Root / "players" =>
        playerService.showListOfPlayers().flatMap(player => Ok(player.asJson))

      case GET -> Root / "players" / IntVar(id) =>
        playerService.findById(Id(id)) flatMap {
          case Some(player) => Ok(player.asJson)
          case None         => NotFound()
        }

      case req @ PUT -> Root / "players"/ id =>
        req.decode[Player] {player =>
          playerService.getInjured(player) flatMap {
            case id: Int => Ok()
            case _       => NotFound()
          }
        }

      case req @ PUT -> Root / "players"/ id =>
        req.decode[Player] { player =>
          playerService.getRecovered(player) flatMap {
            case id: Int => Ok()
            case _       => NotFound()
          }
        }

      case req @ PUT -> Root / "players" / id =>
        req.decode[Player] { player =>
          playerService.givePointsByWeek(player, gameWeek = ???) flatMap {
            case points: Int => Ok(points.asJson)
            case _           => NotFound()
          }
        }

      case req @ PUT -> Root / "players" / id =>
        req.decode[Player] { player =>
          playerService.giveTotalPoints(player) flatMap {
            case points: Int => Ok(points.asJson)
            case _ => NotFound()
          }
        }

    }
  }
}
