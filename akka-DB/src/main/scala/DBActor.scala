import DBActor._
import akka.actor.{Actor, ActorLogging, Props, Stash}

/**
  * Created by zhoudunxiong on 2018/10/5.
  */
class DBActor extends Actor with Stash with ActorLogging {
  override def receive: Receive = {
    case Connected =>
      log.info(s"DB login")
      context.become(connected)
      unstashAll()
    case _ =>
      stash()
  }

  def connected: Receive = {
    case DisConnected =>
      log.info(s"DB logout")
      context.unbecome()
    case DBRead(sql) =>
      log.info(s"DB read sql: $sql")
    case DBWrite(sql) =>
      log.info(s"DB write sql: $sql")
  }
}

object DBActor {
  sealed trait DBOperation
  case class DBWrite(sql: String) extends DBOperation
  case class DBRead(sql: String) extends DBOperation

  sealed trait DBState
  case object Connected extends DBState
  case object DisConnected extends DBState

  def props: Props = Props[DBActor]
}
