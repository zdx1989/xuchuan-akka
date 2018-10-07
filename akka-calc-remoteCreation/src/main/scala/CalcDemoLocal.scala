import Messages._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
object CalcDemoLocal extends App {
  val system = ActorSystem("local")
  import system.dispatcher
  implicit val timeout = Timeout(2.seconds)
  val calc = system.actorOf(Supervisor.props, "supervisor")
  calc ! Num(2)
  calc ! Add(2)
  calc ! Div(0)
  calc ! Add(3)
  (calc ? GetAnswer).mapTo[String].foreach(println)
  scala.io.StdIn.readLine()
  system.terminate()
}
