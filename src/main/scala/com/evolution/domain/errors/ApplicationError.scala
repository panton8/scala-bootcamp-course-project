package com.evolution.domain.errors

sealed abstract class ApplicationError(msg: String) extends Throwable(msg)

final case class SuchUserAlreadyExist(msg: String) extends ApplicationError(msg)
final case object SuchUserDoesNotExist extends ApplicationError("Such user doesn't exist")
