import MoneyCounter.{OneHand, ReportTotal}
import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.ConsistentHashingRouter.ConsistentHashable

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class MoneyCounter extends Actor with ActorLogging {
  var totalAmt: Double = 0.0
  var current: String = "RMB"
  override def receive: Receive = {
    case OneHand(cur, amt) =>
      current = cur
      totalAmt += amt
    case ReportTotal(_) =>
      log.info(s"${self.path.name} has total $totalAmt $current")
  }
}

object MoneyCounter {
  def props: Props = Props[MoneyCounter]

  sealed class Counter(cur: String) extends ConsistentHashable {
    override def consistentHashKey: Any = cur
  }
  case class OneHand(cur: String, amt: Double) extends Counter(cur)
  case class ReportTotal(cur: String) extends Counter(cur)
}