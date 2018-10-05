package backoffSupervisor

import akka.actor.SupervisorStrategy.{Decider, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{Backoff, BackoffSupervisor}
import backoffSupervisor.InnerChild.{ChildException, TestMessage}

import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class InnerChild extends Actor with ActorLogging {
  override def receive: Receive = {
    case m @ TestMessage(msg) =>
      if (Random.nextBoolean())
        throw new ChildException(m)
      else
        log.info(s"InnerChild receive message: $msg")
  }
}

object InnerChild {
  def props: Props = Props[InnerChild]

  case class TestMessage(msg: String)

  class ChildException(val msg: TestMessage) extends Exception
  
  object ChildException {
    def apply(msg: TestMessage): ChildException = new ChildException(msg)

    def unapply(arg: ChildException): Option[TestMessage] = Some(arg.msg)
  }
}

object Supervisor {
  def props: Props = {
    val decider: Decider = {
      case ChildException(tm) =>
        println(s"Message cause exception: ${tm.msg}")
        BackoffSupervisorDemo.sendToParent(tm)
        Restart
    }
    val options = Backoff.onFailure(InnerChild.props, "innerChild", 1.second, 5.seconds, 0.0)
      .withManualReset
      .withSupervisorStrategy(
        OneForOneStrategy(5, 5.seconds) {
          decider orElse SupervisorStrategy.defaultDecider
        }
      )
    BackoffSupervisor.props(options)
  }
}