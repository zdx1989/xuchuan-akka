import FibRoutee.{FibNum, RouteeException}
import akka.actor.{ActorSystem, OneForOneStrategy, SupervisorStrategy}
import akka.actor.SupervisorStrategy.{Decider, Restart}
import akka.routing.BalancingPool

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
object FibRouterDemo extends App {
  val system = ActorSystem("fibRouterSystem")
  val decider: Decider = {
    case _: RouteeException => Restart
  }
  val supervisorStrategy =
    OneForOneStrategy(5, 5.seconds, false) {
      decider orElse SupervisorStrategy.defaultDecider
    }
  val fibRouter = system.actorOf(
    BalancingPool(3)
      .withSupervisorStrategy(supervisorStrategy)
      .withDispatcher("akka.pool-dispatcher")
      .props(FibRoutee.props),
    "balance-pool-router")

  fibRouter ! FibNum(10, 5)
  fibRouter ! FibNum(13, 3)
  fibRouter ! FibNum(15, 3)
  fibRouter ! FibNum(17, 1)

  scala.io.StdIn.readLine()
  system.terminate()
}
