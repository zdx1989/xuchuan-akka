import Messages._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
object CalcDemo extends App {
  val system = ActorSystem("calcSystem")
  val calc = system.actorOf(Supervisor.props, "supervisor")
//  implicit val timeout = Timeout(2.seconds)
//  calc ! Num(2)
//  calc ! Add(2)
//  calc ! Div(0)
//  calc ! Add(3)
//  import system.dispatcher
//  (calc ? GetAnswer).mapTo[String].foreach(println)
  scala.io.StdIn.readLine()
  system.terminate()
}
