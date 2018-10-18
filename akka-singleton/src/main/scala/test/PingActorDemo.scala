package test

import test.PingActor.Disconnect

/**
  * Created by zhoudunxiong on 2018/10/15.
  */
object PingActorDemo extends App {
  PingActor.create(2551)
  PingActor.create()
  PingActor.create()

  val proxy = PingProxy.create()
  proxy ! "ping"
  proxy ! Disconnect

  Thread.sleep(15000)
  proxy ! "ping"


}
