package com.evolution.domain

final case class Player(
  id: Id,
  name: Name,
  surname: Surname,
  price: Price,
  club: Club,
  position: Position,
  role:Role = Ordinary,
  status: Status = Healthy,
  gamePlace: GamePlace = Starter,
  stat: Map[GameWeek,Statistic] = Map()
)
