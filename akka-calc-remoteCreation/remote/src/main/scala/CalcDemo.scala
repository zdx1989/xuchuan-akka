import akka.actor.ActorSystem

/**
  * Created by zhoudunxiong on 2018/10/7.
  */
object CalcDemo extends App {
    val system = ActorSystem("calcSystem")
  val calc = system.actorOf(Supervisor.props, "supervisor")
  scala.io.StdIn.readLine()
  system.terminate()
}
