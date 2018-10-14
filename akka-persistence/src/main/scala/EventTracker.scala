import CalcActor.LogMessage
import akka.actor.{Actor, Props}

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
class EventTracker extends Actor {
  override def receive: Receive = {
    case LogMessage(msg) => println(msg)
  }

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[LogMessage])
    super.preStart()
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self)
    super.postStop()
  }
}

object EventTracker {
  def props: Props = Props[EventTracker]
}