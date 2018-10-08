
import Messages.RegisterBackendActor
import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, RootActorPath, SupervisorStrategy}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberUp}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/8.
  */
class CalcSupervisor(role: String) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  val calcActor = context.actorOf(CalcActor.props, "calc")

  val decider: Decider = {
    case _: ArithmeticException => Resume
  }


  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(5, 5.seconds) {
      decider orElse defaultDecider
    }

  override def preStart(): Unit = {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberUp])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        val frontend = context.actorSelection(RootActorPath(member.address) + "/user/frontend")
        frontend ! RegisterBackendActor(role)
      }
    case msg @ _ => calcActor.forward(msg)
  }
}

object CalcSupervisor {
  def props(role: String): Props = Props(new CalcSupervisor(role))

  def create(role: String): Unit = {
    val config = ConfigFactory.parseString(s"backend.akka.cluster.roles = [\"$role\"]")
      .withFallback(ConfigFactory.load())
      .getConfig("backend")
    val system = ActorSystem("calcClusterSystem", config)
    system.actorOf(CalcSupervisor.props(role), "calcSupervisor")
  }
}
