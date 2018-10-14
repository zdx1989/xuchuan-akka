package test

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import test.Accumulator._

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
class Accumulator extends PersistentActor with ActorLogging {

  var num: Int = 0

  override def receiveRecover: Receive = {
    case Num(i) => num += i
    case SnapshotOffer(_, Num(i)) => num = i
    case RecoveryCompleted =>
      log.info(s"Recovery completed with starting state: $num")
  }

  override def receiveCommand: Receive = {
    case event: Num =>
      persist(event)(handleEvent)
    case GetNum =>
      log.info(s"the num is: $num")
  }

  override def persistenceId: String = "persistence-acc"

  def handleEvent(event: Num): Unit = {
    num += event.i
    if (lastSequenceNr % 2 == 0 && lastSequenceNr != 0)
      saveSnapshot(num)
  }
}

object Accumulator {
  def props: Props = Props[Accumulator]

  case class Num(i: Int)

  case object GetNum
}
