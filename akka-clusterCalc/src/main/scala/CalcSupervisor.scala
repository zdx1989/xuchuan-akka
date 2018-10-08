
import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberEvent, MemberUp}

/**
  * Created by zhoudunxiong on 2018/10/8.
  */
class CalcSupervisor(role: String) extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

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
        val frontend = member
      }
  }
}
