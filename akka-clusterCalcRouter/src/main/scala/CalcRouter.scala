import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
class CalcRouter extends Actor with ActorLogging {
  val router = context.actorOf(FromConfig.props(Props.empty), "calcRouter")

  override def receive: Receive = {
    case msg @ _ => router.forward(msg)
  }
}

object CalcRouter {

  def props: Props = Props[CalcRouter]

  private var router: ActorRef = _

  def create(): Unit = {
    val system = ActorSystem("calcSystem", ConfigFactory.load("hashing"))
    router = system.actorOf(CalcRouter.props, "frontend")
  }

  def getRouter(): ActorRef = router
}
