import CalcActor._
import akka.actor.{ActorLogging, Props}
import akka.persistence._

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
class CalcActor extends PersistentActor with ActorLogging {

  var state: State = State(0)

  override def persistenceId: String = "persistence-actor"

  override def receiveRecover: Receive = {
    case event: Event =>
      state = state.updateState(event)
      context.system.eventStream.publish(LogMessage(s"Restoring event: $event"))
    case SnapshotOffer(metadata, State(res)) =>
      state.copy(result = res)
      context.system.eventStream.publish(LogMessage(s"Restoring snapshot: $metadata"))
    case RecoveryCompleted =>
      log.info(s"Recovery completed with starting state: $state")
  }

  override def receiveCommand: Receive = {
    case Operand(x) => persist(SetNum(x))(handleEvent)
    case Add(x) => persist(Added(x))(handleEvent)
    case Sub(x) => persist(Subtracted(x))(handleEvent)
    case Mul(x) => persist(Multiplied(x))(handleEvent)
    case Div(x) if x != 0 => persist(Divided(x))(handleEvent)

    case ShowResult =>
      context.system.eventStream.publish(LogMessage(s"Current state: $state"))
    case BackupResult =>
      saveSnapshot(state)
      context.system.eventStream.publish(LogMessage(s"Manual saving snapshot: $state"))

    case SaveSnapshotSuccess(metadata) =>
      context.system.eventStream.publish(LogMessage(s"Successfully saved state: $state"))
    case SaveSnapshotFailure(metadata, reason) =>
      context.system.eventStream.publish(LogMessage(s"Saving state: $state failed!"))
  }

  val snapShotInterval = 5

  def handleEvent(evt: Event) = {   //update state and publish progress
    state = state.updateState(evt)
    context.system.eventStream.publish(LogMessage(s"Producing event: $evt"))
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) {
      saveSnapshot(state)
      context.system.eventStream.publish(LogMessage(s"Saving snapshot: $state after $snapShotInterval events"))
    }
  }

  override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.info(s"Persistence Rejected: ${cause.getMessage}")
  }

  override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
    log.info(s"Persistence Error: ${cause.getMessage}")
  }

}

object CalcActor {
  sealed trait Command
  case class Operand(x: Int) extends Command
  case class Add(x: Int) extends Command
  case class Sub(x: Int) extends Command
  case class Mul(x: Int) extends Command
  case class Div(x: Int) extends Command
  case class ShowResult(x: Double) extends Command
  case object BackupResult extends Command

  sealed trait Event
  case class SetNum(x: Int) extends Event
  case class Added(x: Int) extends Event
  case class Subtracted(x: Int) extends Event
  case class Multiplied(x: Int) extends Event
  case class Divided(x: Int) extends Event

  case class State(result: Int) {
    def updateState(evt: Event): State = evt match {
      case SetNum(x) => copy(result = x)
      case Added(x) => copy(result = this.result + x)
      case Subtracted(x) => copy(result = this.result - x)
      case Multiplied(x) => copy(result = this.result * x)
      case Divided(x) => copy(result = this.result / x)
    }
  }

  case class LogMessage(msg: String)
  def props: Props = Props[CalcActor]
}
