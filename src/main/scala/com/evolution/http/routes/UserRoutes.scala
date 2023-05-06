package com.evolution.http.routes

import cats.effect.IO
import com.evolution.domain.Id
import com.evolution.http.domain.UserRegistration
import com.evolution.service.UserService
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl


object UserRoutes {

  def routes(userService: UserService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "users" =>
        userService.showListOfUsers().flatMap(users => Ok(users))

      case req @ POST -> Root / "users"  =>
        req.decode[UserRegistration] { userReg =>
          userService.registration(userReg.userName, userReg.email, userReg.password) flatMap {
            case Some(user) => Ok(user)
            case None       => BadRequest()
          }
        }.handleErrorWith(e => BadRequest(e.getMessage))

      case GET -> Root / "users" / IntVar(id) =>
        userService.findById(Id(id)) flatMap {
          case Some(user) => Ok(user)
          case None       => NotFound()
      }
    }
  }
}

