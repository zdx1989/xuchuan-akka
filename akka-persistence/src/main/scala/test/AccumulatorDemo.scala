package test

import akka.actor.ActorSystem
import test.Accumulator.{GetNum, Num}

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
object AccumulatorDemo extends App {

  val system = ActorSystem("accSystem")
  val acc = system.actorOf(Accumulator.props, "acc")

  acc ! Num(1)
  acc ! Num(2)
  acc ! Num(3)
  acc ! GetNum
}
