import DBActor._
import akka.actor.ActorSystem

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
object DBDemo extends App {
  val system = ActorSystem("DBDemo")
  val dBActor = system.actorOf(DBActor.props, "DBActor")

  dBActor ! DBWrite("UPDATE tableA")
  dBActor ! Connected
  dBActor ! DBRead(s"SELECT * FROM tableA")
  dBActor ! DisConnected

  scala.io.StdIn.readLine()
  system.terminate()
}
