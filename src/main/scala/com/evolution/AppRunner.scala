package com.evolution

import cats.effect.{ExitCode, IO, IOApp}
import com.evolution.domain.{Email, GameWeek, Name, Password}
import com.evolution.service.{PlayerService, TeamService, UserService}

object AppRunner extends IOApp {
  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _        <- IO(println("Choose one:\n1)Sign In\n2)Sigh Up"))
      command1 <- IO.readLine
      _        <- IO(println("userName:\n"))
      userName <- IO.readLine
      _        <- IO(println("email:\n"))
      email    <- IO.readLine
      _        <- IO(println("password:\n"))
      password <- IO.readLine
      _        <- command1 match {
        case "1" => UserService().signIn(Email(email), Password(password)).debug
        case "2" => UserService().registration(Name(userName), Email(email), Password(password)).debug
      }
      _        <- IO(println("###Team creation###"))
      -        <- IO(println("TeamName: "))
      teamName <- IO.readLine
      user     <- UserService().findByEmail(Email(email))
      players  <- PlayerService().showListOfPlayers().debug
      a = for {
      player <- players
    } yield println(s"#  " + player)
      command3 <- IO.readLine
      list     <- IO(command3.split(' ').map(_.toInt))
      _        <- TeamService().createTeam(Name(teamName), user, List(players(list(0)),players(list(1)),players(list(2)), players(list(3)), players(list(4)),players(list(5)),players(list(6)),players(list(7)), players(list(8)), players(list(9)),players(list(10)),players(list(11)),players(list(12)), players(list(13)), players(list(14))), players(list(5)))
      team     <- TeamService().findByName(Name(teamName))
      _        <- TeamService().updateTeamStat(team.get, GameWeek(1))
    } yield ExitCode.Success
  }
}

//31 30 29 28 27 26 24 9 13 12 16 11 25 14 10