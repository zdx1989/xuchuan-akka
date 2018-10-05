import MoneyCounter.{OneHand, ReportTotal}
import akka.actor.ActorSystem
import akka.routing.ConsistentHashingRouter.ConsistentHashMapping
import akka.routing.{BalancingPool, ConsistentHashingPool}

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
object MoneyCounterDemo extends App {
  val system = ActorSystem("moneyCounterSystem")

  def hashMapping: ConsistentHashMapping = {
    case OneHand(cur, _) => cur
    case ReportTotal(cur) => cur
  }

  val router = system.actorOf(
    ConsistentHashingPool(3, virtualNodesFactor = 2)
      .props(MoneyCounter.props),
    "moneyCounterRouter"
  )
  router ! OneHand("RMB", 10)
  router ! OneHand("USD", 10)
  router ! ReportTotal("RMB")
  router ! ReportTotal("USD")

  scala.io.StdIn.readLine()
  system.terminate()
}
