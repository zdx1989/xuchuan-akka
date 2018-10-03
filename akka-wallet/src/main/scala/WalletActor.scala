import WalletActor._
import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by zhoudunxiong on 2018/10/3.
  */
class WalletActor extends Actor with ActorLogging {
  var balance: Double = 0.0
  var zipped: Boolean = true

  override def receive: Receive = {
    case ZipUp =>
      zipped = true
      log.info(s"ZipUp the wallet")
    case UnZip =>
      zipped = false
      log.info(s"UnZip the wallet")
    case PutIn(d) =>
      if (zipped) {
        self ! UnZip
        self ! PutIn(d)
      } else {
        balance += d
        log.info(s"$d put in wallet")
      }
    case DrawOut(d) =>
      if (zipped)
        log.info(s"Wallet had zipped")
      else
        if (balance - d < 0)
          log.info(s"Wallet has not enough money")
        else {
          balance -= d
          log.info(s"$d draw from wallet")
        }
    case CheckBalance =>
      log.info(s"You have $balance in your wallet")
  }
}

object WalletActor {
  sealed trait WalletMsg
  case object ZipUp extends WalletMsg
  case object UnZip extends WalletMsg
  case class PutIn(d: Double) extends WalletMsg
  case class DrawOut(d: Double) extends WalletMsg
  case object CheckBalance extends WalletMsg

  def props: Props = Props[WalletActor]
}
