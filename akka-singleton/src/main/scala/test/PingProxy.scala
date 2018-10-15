package test

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.config.ConfigFactory

/**
  * Created by zhoudunxiong on 2018/10/15.
  */
object PingProxy {

  def create(): ActorRef = {
    val config = ConfigFactory.parseString("akka.cluster.roles=[ping]")
      .withFallback(ConfigFactory.load("ping"))
    val system = ActorSystem("pingSystem", config)
    val proxy = ClusterSingletonProxy.props(
      singletonManagerPath = "/user/singletonManager",
      settings = ClusterSingletonProxySettings(system).withRole(None)
    )
    system.actorOf(proxy, "singletonProxy")
  }
}
