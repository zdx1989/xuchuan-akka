import Messages._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory

/**
  * Created by zhoudunxiong on 2018/10/9.
  */
class CalcRouter extends Actor with ActorLogging {
  var roles: Map[String, ActorRef] = Map()

  override def receive: Receive = {
    case RegisterBackendActor(role) =>
      roles += role -> sender()
      context.watch(sender())
    case add: Add => routeCommand("adder", add)
    case sub: Sub => routeCommand("subor", sub)
    case mul: Mul => routeCommand("muler", mul)
    case div: Div => routeCommand("diver", div)
    case Terminated(ref) =>
      roles = roles.filter { case (_, r) => r != ref}
  }

  def routeCommand(role: String, mathOps: MathOps): Unit = roles.get(role) match {
    case Some(ref) => ref ! mathOps
    case None => log.info(s"role: $role not registered")
  }
}

object CalcRouter {

  private var router: ActorRef = _

  def props: Props = Props[CalcRouter]

  def create(): Unit = {
    val config = ConfigFactory.load().getConfig("frontend")
    val system = ActorSystem("calcClusterSystem", config)
    router = system.actorOf(CalcRouter.props, "frontend")
  }

  def getRoute(): ActorRef = router

}