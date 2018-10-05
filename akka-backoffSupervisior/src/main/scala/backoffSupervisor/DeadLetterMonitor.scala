package backoffSupervisor

import akka.actor.{Actor, ActorLogging, ActorRef, DeadLetter, Props}
import backoffSupervisor.InnerChild.TestMessage

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class DeadLetterMonitor(receiver: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher
  override def receive: Receive = {
    case DeadLetter(msg, sender,_) =>
      context.system.scheduler.scheduleOnce(1.second, receiver, msg.asInstanceOf[TestMessage])
  }
}

object DeadLetterMonitor {
  def props(receiver: ActorRef): Props = Props(classOf[DeadLetterMonitor], receiver)
}