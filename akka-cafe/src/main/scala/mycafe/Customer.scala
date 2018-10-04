package mycafe

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import mycafe.Customer._

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/4.
  */
class Customer(cafe: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher
  override def receive: Receive = {
    case OrderSpecial =>
      log.info("Customer place an order ...")
      cafe ! Cafe.PlaceOrder
    case OrderServed(rcpt) =>
      log.info(s"Customer says: Oh my! got my order ${rcpt.item} for ${rcpt.amt}")
    case ComebackLater =>
      log.info("Customer is not so happy! says: I will be back later!")
      context.system.scheduler.scheduleOnce(1.seconds){ cafe ! Cafe.PlaceOrder }
  }
}

object Customer {
  def props(cafe: ActorRef): Props = Props(classOf[Customer], cafe)

  sealed trait CustomerOrder
  case object OrderSpecial extends CustomerOrder
  case class OrderServed(rcpt: Cafe.Receipt) extends CustomerOrder
  case object ComebackLater extends CustomerOrder
}
