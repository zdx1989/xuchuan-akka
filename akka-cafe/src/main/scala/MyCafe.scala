import akka.actor.{ActorSystem, Props}
import mycafe.{Cafe, Customer}

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/4.
  */
object MyCafe extends App {
  import Customer._
  import scala.concurrent.ExecutionContext.Implicits.global
  val cafeSys = ActorSystem("cafeSystem")
  val cafe = cafeSys.actorOf(Props[Cafe], "cafe")
  val customer = cafeSys.actorOf(Customer.props(cafe), "customer")

  cafeSys.scheduler.schedule(1.second, 1.second, customer, OrderSpecial)

}
