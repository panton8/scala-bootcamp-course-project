package com.evolution.domain
import enumeratum._
sealed trait Club extends EnumEntry

final case object Club extends Enum[Club] with DoobieEnum[Club] {

    final case object Arsenal extends Club
    final case object AstonVilla extends Club
    final case object Bournemouth extends Club
    final case object Brentford extends Club
    final case object Brighton extends Club
    final case object Chelsea extends Club
    final case object CrystalPalace extends Club
    final case object Everton extends Club
    final case object Fulham extends Club
    final case object Leeds extends Club
    final case object Leicester extends Club
    final case object Liverpool extends Club
    final case object ManCity extends Club
    final case object ManUnited extends Club
    final case object Newcastle extends Club
    final case object NottinghamForest extends Club
    final case object Southampton extends Club
    final case object Tottenham extends Club
    final case object WestHam extends Club
    final case object Wolverhampton extends Club

    val values = findValues
}