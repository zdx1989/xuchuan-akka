import akka.actor.ActorSystem
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
object SingletonUser {

  def create(): Unit = {
    val config = ConfigFactory.parseString("akka.cluster.roles=[frontend]")
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("singletonSystem", config)

    val singletonProxy = system.actorOf(ClusterSingletonProxy.props(
      "/user/singletonManager",
      ClusterSingletonProxySettings(system).withRole(None)
    ), name= "singletonUser")

    import system.dispatcher
    //send Dig messages every 2 seconds to SingletonActor through prox
    system.scheduler.schedule(0.seconds, 3.second, singletonProxy, SingletonActor.Dig)

    //send Plant messages every 3 seconds to SingletonActor through prox
    system.scheduler.schedule(1.seconds, 2.second, singletonProxy, SingletonActor.Plant)

    //send kill message to hosting node every 30 seconds
    system.scheduler.schedule(10.seconds, 15.seconds, singletonProxy, SingletonActor.Disconnect)
  }
}
