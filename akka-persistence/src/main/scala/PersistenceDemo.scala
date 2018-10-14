import CalcActor._
import akka.actor.ActorSystem

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
object PersistenceDemo extends App {
  val system = ActorSystem("persistenceSystem")
  system.actorOf(EventTracker.props, "stateTeller")
  val calcActor = system.actorOf(CalcActor.props, "calcActor")

  calcActor ! Add(3)
  calcActor ! Add(7)
  calcActor ! Mul(3)
  calcActor ! Div(2)
  calcActor ! Sub(8)
  calcActor ! Mul(12)
  calcActor ! ShowResult

  scala.io.StdIn.readLine()

  system.terminate()
}
