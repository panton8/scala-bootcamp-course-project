package com.evolution.service

import cats.effect.IO
import com.evolution.domain.errors.SuchPlayerDoesNotExist
import com.evolution.domain.{Club, GameWeek, Id, Name, Player, Position, Statistic, Surname}
import com.evolution.repository._

final case class PlayerService() {

  def showListOfPlayers(): IO[List[Player]] = {
    //implicit val playerOrdering1: Ordering[Player] = Ordering.fromLessThan(_.price.value < _.price.value)
    implicit val playerOrdering2: Ordering[Player] = Ordering.fromLessThan(_.price.value > _.price.value)
    PlayerRepository.listOfPlayers().map(_.sorted(playerOrdering2))
  }

  def showListOfPlayersByPosition(position: Position): IO[List[Player]] =
    PlayerRepository.playersByPosition(position)

  def showListOfPlayersByClub(club: Club): IO[List[Player]] =
    PlayerRepository.playersByClub(club)

  def findByName(name: Name, surname: Surname): IO[Option[Player]] =
    PlayerRepository.playerByNameAndSurname(name, surname)

  def findById(id: Id): IO[Option[Player]] =
    PlayerRepository.playerById(id)

  def addMatchActions(playerId: Id, statistic: Statistic, gameWeek: GameWeek): IO[Option[Int]] =
    PlayerRepository.updateStatistics(statistic, playerId, gameWeek)


  def takeTotalStatistic(playerId: Id): IO[Statistic] =
    PlayerRepository.showTotalPlayerStatistics(playerId)

  def takeWeekStatistic(playerId: Id, gameWeek: GameWeek): IO[Statistic] =
    PlayerRepository.showPlayerStatisticsByWeek(playerId, gameWeek)

  def getInjured(playerId: Id): IO[Int] =
    PlayerRepository.getInjured(playerId)

  def getRecovered(playerId: Id): IO[Int] =
    PlayerRepository.getRecovered(playerId)

  def giveTotalPoints(playerId: Id): IO[Int] = for {
    stat   <- PlayerRepository.showTotalPlayerStatistics(playerId)
    player <- PlayerRepository.playerById(playerId)
    points = player match {
      case Some(player) => Statistic.countPoints(stat, player.position)
      case None         => 0
    }
  } yield points

  def givePointsByWeek(playerId: Id, gameWeek: GameWeek): IO[Int] = for {
    stat <- PlayerRepository.showPlayerStatisticsByWeek(playerId, gameWeek)
    player <- PlayerRepository.playerById(playerId)
    points = player match {
      case Some(player) => Statistic.countPoints(stat, player.position)
      case None         => 0
    }
  } yield points
}