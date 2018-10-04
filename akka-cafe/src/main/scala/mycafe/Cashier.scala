package mycafe

import akka.actor.SupervisorStrategy.Decider
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import mycafe.Cashier.RingRegister
import mycafe.ReceiptPrinter.{PaperJamException, PrintReceipt}

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/4.
  */
class Cashier(kitchen: ActorRef) extends Actor with ActorLogging {
  context.watch(kitchen)
  val printer = context.system.actorOf(ReceiptPrinter.props, "printer")
  val cashierDecider: Decider = {
    case _: PaperJamException => SupervisorStrategy.restart
  }

  override def supervisorStrategy: SupervisorStrategy = {
    OneForOneStrategy(5, 5.seconds){
      cashierDecider orElse SupervisorStrategy.defaultDecider
    }
  }

  val menu = Map[Cafe.Coffee,Double](Cafe.Original -> 5.50,
    Cafe.Cappuccino -> 12.95, Cafe.Espresso -> 11.80)


  override def receive: Receive = {
    case RingRegister(coffee, customer) =>
      log.info(s"Produce receipt for a cup of ${coffee.toString}")
      val amt = menu(coffee)
      val rcpt = Cafe.Receipt(coffee.toString,amt)
      printer ! PrintReceipt(customer, rcpt)
      sender() ! Cafe.Sold(rcpt)
    case Terminated(_) =>
      log.info("Cashier says: Oh, kitchen is closed. Let's make the end of day!")
      context.system.terminate()
  }
}

object Cashier {
  def props(kitchen: ActorRef): Props = Props(classOf[Cashier], kitchen)

  case class RingRegister(cup: Cafe.Coffee, customer: ActorRef)
}