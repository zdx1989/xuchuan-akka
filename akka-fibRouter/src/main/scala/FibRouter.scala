import FibRoutee.{FibNum, GetAnswer, RouteeException}
import akka.actor.{Actor, ActorLogging, Props}

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class FibRoutee extends Actor with ActorLogging {
  import context.dispatcher
  override def receive: Receive = {
    case FibNum(n, delay) =>
      context.system.scheduler.scheduleOnce(delay.seconds, self, GetAnswer(n))
    case GetAnswer(n) =>
      if (Random.nextBoolean())
        throw new RouteeException
      else
        log.info(s"fib($n) = ${fib(n)}")
  }

  def fib(n: Int): Int = {
    @tailrec
    def help(n: Int, b: Int, a: Int): Int = n match {
      case 0 => a
      case _ =>
        help(n - 1, a + b, b)
    }
    help(n, 1, 0)
  }

  override def postStop(): Unit = {
    log.info(s"fibRoutee: ${self.path.name} stopped")
    super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"fibRoutee: ${self.path.name} restarting for ${reason.getMessage}")
    message.foreach(self ! _)
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"fibRoutee: ${self.path.name} restarted for ${reason.getMessage}")
    super.postRestart(reason)
  }


}

object FibRoutee {
  def props: Props = Props[FibRoutee]

  case class FibNum(n: Int, delay: Int)

  class RouteeException extends Exception

  case class GetAnswer(n: Int)
}