package com.evolution.http.auth

import cats.data._
import cats.effect.IO
import com.evolution.domain.errors.AuthenticationError
import com.evolution.domain.{Email, Password, User}
import com.evolution.service.UserService
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.{AuthedRequest, _}
import org.http4s.implicits.http4sHeaderSyntax
import org.http4s.server.AuthMiddleware

object Auth {

  def getAuthUser(authHeader: String): IO[Option[User]] = {
      val userData = BasicCredentials(authHeader.substring(6))
      UserService().signIn(Email(userData.username), Password(userData.password))
  }

  def authUser: Kleisli[IO, Request[IO], Either[AuthenticationError, User]] = Kleisli { request: Request[IO] =>
    request.headers.get[Authorization] match {
      case Some(h) => getAuthUser(h.value).map(_.toRight(AuthenticationError("Access denied")))
      case None    => IO.pure(Left(AuthenticationError("Access denied")))
    }
  }

  def inAuthFailure: AuthedRoutes[AuthenticationError, IO] = Kleisli { request: AuthedRequest[IO, AuthenticationError] =>
    OptionT.liftF(Forbidden(request.context))
  }

  def authMiddleware: AuthMiddleware[IO, User] = AuthMiddleware(authUser, inAuthFailure)
}
