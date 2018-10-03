package mycafe

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import mycafe.ReceiptPrinter.{PaperJamException, PrintReceipt}

import scala.util.Random

/**
  * Created by zhoudunxiong on 2018/10/3.
  */
class ReceiptPrinter extends Actor with ActorLogging {
  var paperJammed: Boolean = false

  override def receive: Receive = {
    case PrintReceipt(customer, r) =>
      if (Random.nextInt(6) % 6 == 0) {
        log.info(s"Printer jammed paper")
        paperJammed = true
        throw new PaperJamException
      } else {
        log.info(s"Print receipt $r and send to $customer")
        customer ! r
      }
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"Restarting ReceiptPrinter for ${reason.getMessage}...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"Started ReceiptPrinter for ${reason.getMessage}.")
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    log.info("Stopped ReceiptPrinter.")
    super.postStop()
  }
}

object ReceiptPrinter {
  case class PrintReceipt(sendTo: ActorRef, receipt: Cafe.Receipt)

  class PaperJamException extends Exception

  def props: Props = Props[ReceiptPrinter]
}
