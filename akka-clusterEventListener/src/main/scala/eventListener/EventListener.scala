package eventListener

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

/**
  * Created by zhoudunxiong on 2018/10/8.
  */
class EventListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)


  override def preStart(): Unit = {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    super.preStart()
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    super.postStop()
  }

  override def receive: Receive = {
    case MemberJoined(member) =>
      log.info(s"member is joining: ${member.address}")
    case MemberUp(member) =>
      log.info(s"member is up: ${member.address}")
    case MemberLeft(member) =>
      log.info(s"member is leaving: ${member.address}")
    case MemberExited(member) =>
      log.info(s"member is exiting: ${member.address}")
    case MemberRemoved(member, previousStatus) =>
      log.info(s"member is removed: ${member.address} after $previousStatus")
    case UnreachableMember(member) =>
      log.info(s"member is unreachable: ${member.address}")
      cluster.down(member.address)
  }
}

object EventListener {
  def props: Props = Props[EventListener]
}
