package mycafe

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.BackoffSupervisor
import mycafe.Chef.{ChefBusy, MakeSpecial}

import scala.util.Random

/**
  * Created by zhoudunxiong on 2018/10/3.
  */
class Chef extends Actor with ActorLogging {
  log.info(s"Chef says: I'm ready to work...")
  var chefBusy: Boolean = false
  var currentSpecial: Cafe.Coffee = Cafe.original
  val specials = Map(0 -> Cafe.Original,1 -> Cafe.Espresso, 2 -> Cafe.Cappuccino)

  override def receive: Receive = {
    case MakeSpecial =>
      if (Random.nextInt(6) % 6 == 0) {
        log.info(s"Chef is busying")
        chefBusy = true
        throw new ChefBusy("Busy!")
      } else {
        currentSpecial = randomSpecial
        log.info(s"Chef says current Special is $currentSpecial")
        sender() ! currentSpecial
      }
  }

  def randomSpecial = specials(Random.nextInt(specials.size))

  override def postStop(): Unit = {
    log.info(s"Chef had Stopped")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"Chef is restarting for ${reason.getMessage}")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"Chef had restarted for ${reason.getMessage}")
    context.parent ! BackoffSupervisor.Reset
    super.postRestart(reason)
  }
}

object Chef {
  def props: Props = Props[Chef]

  sealed trait Order
  case object MakeSpecial extends Order
  case class ChefBusy(msg: String) extends Exception(msg)
}