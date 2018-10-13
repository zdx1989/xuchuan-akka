
import akka.actor.SupervisorStrategy._
import akka.actor._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
class CalcSupervisor extends Actor with ActorLogging {

  val calc = context.system.actorOf(CalcActor.props, "calc")

  val decider: Decider = {
    case _: ArithmeticException => Resume
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(5, 5.seconds) {
      decider orElse defaultDecider
    }

  override def receive: Receive = {
    case msg @ _ => calc.forward(msg)
  }
}

object CalcSupervisor {
  def props: Props = Props[CalcSupervisor]

  def create(port: Int): Unit = {
    val config = ConfigFactory.parseString("akka.cluster.roles=[backend]")
      .withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port"))
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("calcSystem", config)
    val clac = system.actorOf(CalcSupervisor.props, "calcSupervisor")
  }
}
