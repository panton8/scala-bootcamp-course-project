package com.evolution.service

import cats.effect.IO
import com.evolution.domain.{Club, GameWeek, Id, Name, Player, Position, Statistic, Surname}
import com.evolution.repository._

final case class PlayerService() {
  def showListOfPlayers(): IO[List[Player]] = {
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

  def addMatchActions(player: Player, statistic: Statistic): IO[Option[Int]] =
    PlayerRepository.updateStatistics(statistic, player.id)


  def takeTotalStatistic(player: Player): IO[Statistic] =
    PlayerRepository.showTotalPlayerStatistics(player.id)

  def takeWeekStatistic(player: Player, gameWeek: GameWeek): IO[Statistic] =
    PlayerRepository.showPlayerStatisticsByWeek(player.id, gameWeek)

  def getInjured(player: Player): IO[Int] =
    PlayerRepository.getInjured(player.id)

  def getRecovered(player: Player): IO[Int] =
    PlayerRepository.getRecovered(player.id)

  def giveTotalPoints(player: Player): IO[Int] = for {
    stat <- PlayerRepository.showTotalPlayerStatistics(player.id)
    points = Statistic.countPoints(stat)
  } yield points

  def givePointsByWeek(player: Player, gameWeek: GameWeek): IO[Int] = for {
    stat <- PlayerRepository.showPlayerStatisticsByWeek(player.id, gameWeek)
    point = Statistic.countPoints(stat, player.status)
  } yield point
}