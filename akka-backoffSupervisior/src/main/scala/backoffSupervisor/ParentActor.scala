package backoffSupervisor

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class ParentActor extends Actor with ActorLogging {
  val supervisor = context.actorOf(Supervisor.props, "supervisor")

  override def receive: Receive = {
    case msg @ _ =>
      supervisor ! msg
  }
}

object ParentActor {
  def props: Props = Props[ParentActor]

}
