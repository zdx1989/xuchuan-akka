package eventListener

import akka.actor.ActorSystem
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

/**
  * Created by zhoudunxiong on 2018/10/8.
  */
object EventListenerDemo {

  def main(args: Array[String]): Unit = {
    val port =
    if (args.isEmpty) "0"
    else args(0)

    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port = $port")
    .withFallback(ConfigFactory.load())

    val clusterSystem = ActorSystem("eventListenerSystem",config)
    val eventListener = clusterSystem.actorOf(EventListener.props,"eventListener")

    val cluster = Cluster(clusterSystem)
    cluster.registerOnMemberRemoved(println("Leaving cluster. I should cleanup... "))
    cluster.registerOnMemberUp(println("Hookup to cluster. Do some setups ..."))
    println("actor system started!")
    scala.io.StdIn.readLine()

    clusterSystem.terminate()

  }
}
