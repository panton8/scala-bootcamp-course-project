package com.evolution.http.routes

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import com.evolution.domain.Access.{Admin, Base}
import com.evolution.domain.errors.SuchUserDoesNotExist
import com.evolution.domain.{Email, Id, User}
import com.evolution.http.auth.Auth.authMiddleware
import com.evolution.http.domain.{UserRegistration, UserSignIn}
import com.evolution.service.UserService
import org.http4s.{AuthedRoutes, HttpRoutes, Response}
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

final case class UserRoutes(userService: UserService) {

  private def authedRoutes: AuthedRoutes[User, IO] = AuthedRoutes.of {
      case req @ DELETE -> Root / "users" as user =>
        user.access match {
          case Base  => IO(Response(Forbidden))
          case Admin => req.req.decode[Email] { email =>
            Ok(userService.deleteUser(email))
          }.handleErrorWith(e => BadRequest(e.getMessage))
        }

      case GET -> Root / "users" as user =>
        userService.showListOfUsers().flatMap(users => Ok(users))

      case GET -> Root / "users" / IntVar(id) as user =>
        userService.findById(Id(id)) flatMap {
          case Some(user) => Ok(user)
          case None       => NotFound()
        }
    }

  private def loginRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case req @ GET -> Root / "users" / "login" =>
        req.decode[UserSignIn] { newUser =>
          userService.signIn(newUser.email, newUser.password) flatMap {
            case Some(user) => Ok(user)
            case None       => BadRequest(SuchUserDoesNotExist.getMessage)
          }
        }

      case req @ POST -> Root / "users" / "register" =>
        req.decode[UserRegistration] { userReg =>
          userService.registration(userReg.userName, userReg.email, userReg.password) flatMap {
            case Some(user) => Created(user)
            case None       => BadRequest()
          }
        }
    }

  val routes: HttpRoutes[IO] = loginRoutes <+> authMiddleware.apply(authedRoutes)
}
