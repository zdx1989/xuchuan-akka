import CalcActor._
import akka.actor.{ActorLogging, Props}
import akka.cluster.Cluster
import akka.persistence.{PersistentActor, SnapshotOffer}

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
class CalcActor extends PersistentActor with ActorLogging {

  val cluster = Cluster(context.system)

  var state = State(0)

  def updateState(event: Event): Unit =
    state = state.updateState(event)

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  override def receiveRecover: Receive = {
    case event: Event => state.updateState(event)
    case SnapshotOffer(_, State(res)) => state = state.copy(result = res)
  }

  override def receiveCommand: Receive = {
    case num: Num =>
      val res = getResult(state.result, num)
      persist(SetResult(res))(updateState)
    case add: Add =>
      val res = getResult(state.result, add)
      persist(SetResult(res))(updateState)
    case sub: Sub =>
      val res = getResult(state.result, sub)
      persist(SetResult(res))(updateState)
    case mul: Mul =>
      val res = getResult(state.result, mul)
      persist(SetResult(res))(updateState)
    case div: Div =>
      val res = getResult(state.result, div)
      persist(SetResult(res))(updateState)
    case ShowResult =>
      log.info(s"result in ${cluster.selfAddress.port} is: ${state.result}")
    case DisConnect =>
      log.info(s"${cluster.selfAddress} is leaving cluster!!!")
      cluster.leave(cluster.selfAddress)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"calcActor is restarting for ${reason.getMessage}")
    super.postRestart(reason)
  }
}

object CalcActor {
  sealed trait Command
  case class Num(d: Double) extends Command
  case class Add(d: Double) extends Command
  case class Sub(d: Double) extends Command
  case class Mul(d: Double) extends Command
  case class Div(d: Double) extends Command
  case object ShowResult extends Command
  case object DisConnect extends Command

  sealed trait Event
  case class SetResult(a: Any) extends Event

  case class State(result: Double) {
    def updateState(event: Event): State = event match {
      case SetResult(a) => copy(result = a.asInstanceOf[Double])
    }
  }

  def getResult(res: Double, command: Command): Any = command match {
    case Num(d) => res
    case Add(d) => res + d
    case Sub(x) => res - x
    case Mul(x) => res * x
    case Div(x) =>
      val _ = res.toInt / x.toInt
      res / x
    case _ => new ArithmeticException("Invalid Operation!")
  }

  def props: Props = Props[CalcActor]
}