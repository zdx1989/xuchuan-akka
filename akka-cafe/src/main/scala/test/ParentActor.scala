package test

import akka.actor.SupervisorStrategy.Decider
import akka.actor._
import test.ChildActor.ChildBusyException

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class ParentActor extends Actor with ActorLogging {
  val decider: Decider = {
    case _ : ChildBusyException => SupervisorStrategy.Restart
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(5, 5.second, false){
      decider orElse SupervisorStrategy.defaultDecider
    }
  }

  val childActor = context.actorOf(ChildActor.props, "child")
  override def receive: Receive = {
    case msg: String =>
      childActor ! msg
  }
}

object ParentActor {
  def props: Props = Props[ParentActor]
}
