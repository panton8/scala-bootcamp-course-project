package com.evolution.domain

trait Club {
  def toString: String
}

object Arsenal extends Club {override def toString: String = "Arsenal"}
object AstonVilla extends Club {override def toString: String = "AstonVilla"}
object Bournemouth extends Club {override def toString: String = "Bournemouth"}
object Brentford extends Club {override def toString: String = "Brentford"}
object Brighton extends Club {override def toString: String = "Brighton"}
object Chelsea extends Club {override def toString: String = "Chelsea"}
object CrystalPalace extends Club {override def toString: String = "CrystalPalace"}
object Everton extends Club {override def toString: String = "Everton"}
object Fulham extends Club {override def toString: String = "Fulham"}
object Leeds extends Club {override def toString: String = "Leeds"}
object Leicester extends Club {override def toString: String = "Leicester"}
object Liverpool extends Club {override def toString: String = "Liverpool"}
object ManCity extends Club {override def toString: String = "ManCity"}
object ManUnited extends Club {override def toString: String = "ManUnited"}
object Newcastle extends Club {override def toString: String = "Newcastle"}
object NottinghamForest extends Club {override def toString: String = "NottinghamForest"}
object Southampton extends Club {override def toString: String = "Southampton"}
object Tottenham extends Club {override def toString: String = "Tottenham"}
object WestHam extends Club {override def toString: String = "WestHam"}
object Wolverhampton extends Club {override def toString: String = "Wolverhampton"}

object ClubSyntax {
  implicit class GetClub(val club: String) extends AnyVal {
    def toClub: Club  = club match {
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
    }
  }
}