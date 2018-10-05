package backoffSupervisor

import akka.actor.{ActorSystem, DeadLetter}
import backoffSupervisor.InnerChild.TestMessage

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
object BackoffSupervisorDemo extends App {
  val system = ActorSystem("testSystem")
  val parent = system.actorOf(ParentActor.props, "parent")
  val deadLetterMonitorActor = system.actorOf(DeadLetterMonitor.props(parent), "dlMonitor")
  system.eventStream.subscribe(deadLetterMonitorActor,classOf[DeadLetter])
  parent ! TestMessage("Hello message 1 to supervisor")
  parent ! TestMessage("Hello message 2 to supervisor")
  parent ! TestMessage("Hello message 3 to supervisor")


  scala.io.StdIn.readLine()

  system.terminate()

  def sendToParent(msg: TestMessage) = parent ! msg
}
