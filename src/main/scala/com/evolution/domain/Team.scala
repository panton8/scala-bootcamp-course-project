package com.evolution.domain

final case class Team(
  id: Id,
  name: Name,
  players: List[Player],
  points: Points = Points(0),
  freeTransfers: Transfer = Transfer(0)
)
