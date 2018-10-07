
import akka.actor.SupervisorStrategy.Decider
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
class Supervisor extends Actor with ActorLogging {

  val calcActor = context.actorOf(CalcActor.props, "calc")

  override def receive: Receive = {
    case msg @ _ => calcActor.forward(msg)
  }

  val decider: Decider = {
    case _: ArithmeticException => SupervisorStrategy.Resume
  }


  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(5, 5.seconds, false) {
      decider orElse SupervisorStrategy.defaultDecider
    }
}

object Supervisor {
  def props: Props = Props[Supervisor]
}
