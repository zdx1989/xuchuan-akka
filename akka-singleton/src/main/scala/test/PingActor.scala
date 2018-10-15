package test

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import com.typesafe.config.ConfigFactory
import test.PingActor._

/**
  * Created by zhoudunxiong on 2018/10/15.
  */
class PingActor extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def receive: Receive = {
    case "ping" =>
      log.info(s"${self.path.address} say pong!")
    case Disconnect =>
      cluster.leave(self.path.address)
  }
}

object PingActor {
  case object Disconnect

  def props: Props = Props[PingActor]

  def create(port: Int = 0): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString(s"akka.cluster.roles=[ping]"))
      .withFallback(ConfigFactory.load("ping"))
    val system = ActorSystem("pingSystem", config)
    val manager = ClusterSingletonManager.props(
      singletonProps = props,
      terminationMessage = Disconnect,
      settings = ClusterSingletonManagerSettings(system).withRole(Some("ping"))
    )
    system.actorOf(manager, "singletonManager")
  }
}
