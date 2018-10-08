import Messages._
import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by zhoudunxiong on 2018/10/8.
  */
class CalcActor extends Actor with ActorLogging {

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"calcActor is restarting for ${reason.getMessage}")
    super.preRestart(reason, message)
  }

  override def receive: Receive = {
    case Add(x, y) =>
      log.info(s"$x + $y is calc by $self and result is ${x + y}")
    case Sub(x, y) =>
      log.info(s"$x - $y is calc by $self and result is ${x - y}")
    case Mul(x, y) =>
      log.info(s"$x * $y is calc by $self and result is ${x * y}")
    case Div(x, y) =>
      log.info(s"$x / $y is calc by $self and result is ${x / y}")
  }
}

object CalcActor {
  def props: Props = Props[CalcActor]
}
