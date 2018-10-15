import SingletonActor._
import akka.actor._
import akka.pattern.ask
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.persistence.journal.leveldb._
import akka.persistence.{PersistentActor, SnapshotOffer}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/13.
  */
class SingletonActor extends PersistentActor with ActorLogging {

  var freeHoles = 0
  var freeTrees = 0
  var ttlMatches = 0

  val cluster = Cluster(context.system)

  override def persistenceId: String = self.path.parent.name + "_" + self.path.name

  override def receiveRecover: Receive = {
    case event: Event => updateState(event)
    case SnapshotOffer(_, s: State) =>
      freeHoles = s.nHoles
      freeTrees = s.nTrees
      ttlMatches = s.nMatches
  }

  override def receiveCommand: Receive = {
    case Dig =>
      persist(AddHole)(updateState)
      sender() ! AckDig
      log.info(s"State on ${cluster.selfAddress}:freeHoles=$freeHoles,freeTrees=$freeTrees,ttlMatches=$ttlMatches")
    case Plant =>
      persist(AddTree)(updateState)
      sender() ! AckPlant
      log.info(s"State on ${cluster.selfAddress}:freeHoles=$freeHoles,freeTrees=$freeTrees,ttlMatches=$ttlMatches")
    case Disconnect =>
      log.info(s"${cluster.selfAddress} is leaving cluster ...")
      cluster.leave(cluster.selfAddress)
    case CleanUp =>
      self ! PoisonPill
  }

  def updateState(event: Event): Unit = event match {
    case AddHole =>
      if (freeTrees > 0) {
        ttlMatches += 1
        freeTrees -= 1
      } else freeHoles += 1
    case AddTree =>
      if (freeHoles > 0) {
        ttlMatches += 1
        freeHoles -= 1
      } else freeTrees += 1
  }

}

object SingletonActor {
  sealed trait Event
  case object AddHole extends Event
  case object AddTree extends Event

  case class State(nHoles: Int, nTrees: Int, nMatches: Int)

  sealed trait Command
  case object Dig extends Command
  case object Plant extends Command
  case object AckDig extends Command
  case object AckPlant extends Command
  case object Disconnect extends Command
  case object CleanUp extends Command

  def create(port: Int = 0): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.cluster.roles=[singleton]"))
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("singletonSystem", config)
    val path = ActorPath.fromString("akka.tcp://singletonSystem@127.0.0.1:2551/user/store")
    startupSharedJournal(system, port == 2551, path)
    val singletonManager = system.actorOf(ClusterSingletonManager.props(
        Props[SingletonActor],
        CleanUp,
        ClusterSingletonManagerSettings(system).withRole(Some("singleton"))
      ), name = "singletonManager")

  }

  def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")
    // register the shared journal
    import system.dispatcher
    implicit val timeout = Timeout(15.seconds)
    val f = system.actorSelection(path) ? Identify(None)
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) =>
        SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }
}
