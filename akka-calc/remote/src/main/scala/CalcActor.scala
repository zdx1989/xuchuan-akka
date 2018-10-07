import Messages._
import akka.actor.{Actor, ActorLogging, Props}

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
class CalcActor extends Actor with ActorLogging {
  var result: Double = 0.0

  override def receive: Receive = {
    case Num(n) => result = n
    case Add(n) => result += n
    case Sub(n) => result -= n
    case Mul(n) => result *= n
    case Div(n) =>
      val _ = result.toInt / n.toInt
      result /= n
    case Clear => result = 0.0
    case GetAnswer =>
      sender() ! s"the result is: $result"
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"clac is restarting for ${reason.getMessage}")
    super.preRestart(reason, message)
  }
}

object CalcActor {
  def props: Props = Props[CalcActor]
}
