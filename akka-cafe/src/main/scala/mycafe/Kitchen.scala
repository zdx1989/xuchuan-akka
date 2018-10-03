package mycafe

import akka.actor.SupervisorStrategy.{Decider, Restart}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{Backoff, BackoffSupervisor}
import mycafe.Chef.ChefBusy

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/3.
  */
class Kitchen extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg @ _ =>
      context.children.foreach { chef =>
        chef forward msg
      }
  }

  override def postStop(): Unit = {
    log.info(s"Kitchen closed")
    super.postStop()
  }
}

object Kitchen {
  def props: Props = Props[Kitchen]

  def kitchenDecider: Decider = {
    case _ : ChefBusy => Restart
  }

  def kitchenProps: Props = {
    val options = Backoff.onFailure(
      Chef.props,
      "chef",
      1.seconds,
      5.seconds,
      0.0)
      .withManualReset
      .withSupervisorStrategy(
        OneForOneStrategy(5, 5.seconds){
          kitchenDecider orElse SupervisorStrategy.defaultDecider
        }
      )
    BackoffSupervisor.props(options)
  }
}