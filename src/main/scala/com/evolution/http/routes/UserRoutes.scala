package com.evolution.http.routes

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import com.evolution.domain.Access.{Admin, Base}
import com.evolution.domain.{Email, Id, User}
import com.evolution.http.auth.Auth.{authUser, inAuthFailure}
import com.evolution.http.domain.UserRegistration
import com.evolution.service.UserService
import org.http4s.{AuthedRoutes, HttpRoutes, Response}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io._
import org.http4s.server.AuthMiddleware

object UserRoutes {
  private val userService: UserService = UserService()

  val middleware: AuthMiddleware[IO, User] =
    AuthMiddleware(authUser, inAuthFailure)

  val service: HttpRoutes[IO] = middleware(authedRoutes(userService)) <+> routes(userService)

  def authedRoutes(userService: UserService): AuthedRoutes[User, IO] =
    AuthedRoutes.of {
      case req @ DELETE -> Root / "users" as user =>
         user.access match {
          case Base  => IO(Response(Unauthorized))
          case Admin => req.req.decode[Email] { email =>
            Ok(userService.deleteUser(email)).handleErrorWith(e => BadRequest(e.getMessage))
          }
        }
    }

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
