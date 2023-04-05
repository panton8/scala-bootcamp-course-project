package com.evolution.domain

sealed trait Club {
  val club : String
}

final case object Arsenal extends Club {override val club: String = "Arsenal"}
final case object AstonVilla extends Club {override val club: String = "AstonVilla"}
final case object Bournemouth extends Club {override val club: String = "Bournemouth"}
final case object Brentford extends Club {override val club: String = "Brentford"}
final case object Brighton extends Club {override val club: String = "Brighton"}
final case object Chelsea extends Club {override val club: String = "Chelsea"}
final case object CrystalPalace extends Club {override val club: String = "CrystalPalace"}
final case object Everton extends Club {override val club: String = "Everton"}
final case object Fulham extends Club {override val club: String = "Fulham"}
final case object Leeds extends Club {override val club: String = "Leeds"}
final case object Leicester extends Club {override val club: String = "Leicester"}
final case object Liverpool extends Club {override val club: String = "Liverpool"}
final case object ManCity extends Club {override val club: String = "ManCity"}
final case object ManUnited extends Club {override val club: String = "ManUnited"}
final case object Newcastle extends Club {override val club: String = "Newcastle"}
final case object NottinghamForest extends Club {override val club: String = "NottinghamForest"}
final case object Southampton extends Club {override val club: String = "Southampton"}
final case object Tottenham extends Club {override val club: String = "Tottenham"}
final case object WestHam extends Club {override val club: String = "WestHam"}
final case object Wolverhampton extends Club {override val club: String = "Wolverhampton"}

object Club {
  def parse(club: String): Club = club match {
      case "Arsenal"          => Arsenal
      case "AstonVilla"       => AstonVilla
      case "Bournemouth"      => Bournemouth
      case "Brentford"        => Brentford
      case "Brighton"         => Brighton
      case "Chelsea"          => Chelsea
      case "CrystalPalace"    => CrystalPalace
      case "Everton"          => Everton
      case "Fulham"           => Fulham
      case "Leeds"            => Leeds
      case "Leicester"        => Leicester
      case "Liverpool"        => Liverpool
      case "ManCity"          => ManCity
      case "ManUnited"        => ManUnited
      case "Newcastle"        => Newcastle
      case "NottinghamForest" => NottinghamForest
      case "Southampton"      => Southampton
      case "Tottenham"        => Tottenham
      case "WestHam"          => WestHam
      case "Wolverhampton"    => Wolverhampton
      case _                  => ???
    }
}