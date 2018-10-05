package test

import akka.actor.{Actor, ActorLogging, Props}
import test.ChildActor.ChildBusyException

import scala.util.Random

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class ChildActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg: String =>
      if (Random.nextBoolean())
        throw ChildBusyException("ChildActor is busing")
      else
        log.info(s"ChildActor receive a message: $msg")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"ChildActor is restarting for ${reason.getMessage}")
    message match {
      case Some(msg) =>
        log.info(s"Exception: $msg")
        self ! msg
    }
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"ChildActor restarted for ${reason.getMessage}")
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    log.info(s"ChildActor stopped")
    super.postStop()
  }
}

object ChildActor {
  def props: Props = Props[ChildActor]

  case class ChildBusyException(msg: String) extends Exception(msg)
}
