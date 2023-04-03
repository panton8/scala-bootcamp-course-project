package com.evolution.domain

import doobie.util.{Read, Write}

final case class Team(
  id: Id,
  name: Name,
  points: Points = Points(0),
  freeTransfers: Transfer = Transfer(2),
  players: List[Player] = Nil
)

object Team {
  implicit val teamRead: Read[Team] = Read[(Int, String, Int, Int)].map {
    case (id, name, points, transfers_amount) => Team(Id(id), Name(name), Points(points), Transfer(transfers_amount))
  }

  implicit val teamWrite: Write[Team] = Write[(Int, String, Int, Int)].contramap { team =>
    (team.id.value, team.name.value, team.points.value, team.freeTransfers.value)
  }
}