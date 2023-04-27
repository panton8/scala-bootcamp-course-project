package com.evolution.http.routes

import cats.effect.IO
import com.evolution.domain.{Id, User}
import com.evolution.http.domain.UserRegistration
import com.evolution.service.UserService
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import io.circe.Json
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl


object UserRoutes {

  def routes(userService: UserService): HttpRoutes[IO] = {
    val dsl = Http4sDsl[IO]
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "users" =>
        userService.showListOfUsers.flatMap(users => Ok(users))

      case req @ POST -> Root / "users" =>
        req.decode[UserRegistration] { user =>
          userService.registration(user.userName, user.email, user.password) flatMap(id =>
            Created(Json.obj(("id", Json.fromInt(id))))
            )
        }

      case GET -> Root / "users" / IntVar(id) =>
        userService.findById(Id(id)) flatMap {
          case None       => NotFound()
          case Some(user) => Ok(user)
      }

      case GET -> Root / "users" / userName =>
        userService.signIn(email = ???, password = ???) flatMap {
          case None       => NotFound()
          case Some(user) => Ok()

        }

    }
  }
}

