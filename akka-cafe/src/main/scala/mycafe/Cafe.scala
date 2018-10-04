package mycafe

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.AskTimeoutException
import akka.util.Timeout
import akka.pattern._
import mycafe.Cafe.{Coffee, PlaceOrder, Sold}
import mycafe.Cashier.RingRegister

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/4.
  */
class Cafe extends Actor with ActorLogging {
  import context.dispatcher
  implicit val timeout = Timeout(2.seconds)
  var totalAmount: Double = 0.0
  val kitchen = context.actorOf(Kitchen.props, "kitchen")
  val chef = context.actorSelection("/user/cafe/kitchen/chef")
  val cashier = context.actorOf(Cashier.props(kitchen), "cashier")
  var customer: ActorRef = _

  override def receive: Receive = {
    case Sold(rcpt) =>
      totalAmount += rcpt.amt
      log.info(s"Today's sales is up to $totalAmount")
      customer ! Customer.OrderServed(rcpt)
      if (totalAmount > 100.00) {
        log.info("Asking kichen to clean up ...")
        context.stop(kitchen)
      }
    case PlaceOrder =>
      customer = sender()
      val sold = (for {
        item <- (chef ? Chef.MakeSpecial).mapTo[Coffee]
        sales <- (cashier ? RingRegister(item, sender())).mapTo[Sold]
      } yield Sold(sales.receipt)).mapTo[Sold]
        .recover {
          case _: AskTimeoutException => Customer.ComebackLater
        }.pipeTo(self)
  }
}

object Cafe {
  sealed trait Coffee
  case object Original extends Coffee
  case object Espresso extends Coffee
  case object Cappuccino extends Coffee

  case class Receipt(item: String, amt: Double)

  sealed trait Routine
  case object PlaceOrder extends Routine
  case class Sold(receipt: Receipt) extends Routine
}