package test

import akka.actor.ActorSystem

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
object TestMain extends App {
  val system = ActorSystem("testSystem")
  val parentActor = system.actorOf(ParentActor.props, "parent")

  parentActor ! "Hello 1"
  parentActor ! "Hello 2"
  parentActor ! "Hello 3"
  parentActor ! "Hello 4"

  scala.io.StdIn.readLine()
  system.terminate()
}
