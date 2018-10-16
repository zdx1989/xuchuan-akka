package test

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor

/**
  * Created by zhoudunxiong on 2018/10/16.
  */

object AddActor {
  sealed trait Ops

  case class Add(n: Int) extends Ops

  case object GetRequest

  def props: Props = Props[AddActor]
}

class AddActor extends PersistentActor with ActorLogging {
  import AddActor._

  var res: Int = 0

  override def receiveRecover: Receive = {
    case add @ Add(n) => res += n
  }

  override def receiveCommand: Receive = {
    case add @ Add(n) =>
      persist(add) { add => res += add.n }
    case getRes @ GetRequest =>
      sender() ! res
  }

  override def persistenceId: String = "add-actor"
}
