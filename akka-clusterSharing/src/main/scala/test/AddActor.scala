package test

import akka.actor.{ActorIdentity, ActorLogging, ActorPath, ActorSystem, Identify, Props}
import akka.cluster.Cluster
import akka.pattern.ask
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import akka.persistence.PersistentActor
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/16.
  */

object AddActor {
  sealed trait Ops

  case class Add(n: Int) extends Ops

  case object GetRequest extends Ops

  case object DisConnect extends Ops

  def props: Props = Props[AddActor]

  case class Command(id: String, msg: Ops)

  val entityId: ExtractEntityId = {
    case Command(id, msg) => (id, msg)
  }

  val shardId: ExtractShardId = {
    case Command(id, _) => id
  }

  val shardName: String = "add"

}

class AddActor extends PersistentActor with ActorLogging {
  import AddActor._

  var res: Int = 0

  val cluster = Cluster(context.system)

  override def receiveRecover: Receive = {
    case Add(n) => res += n
  }

  override def receiveCommand: Receive = {
    case add @ Add(n) =>
      persist(add) { add => res += add.n }
    case GetRequest =>
      log.info(s"${self.path}: $res")
    case DisConnect =>
      log.info(s"${cluster.selfAddress} is leaving cluster!!!")
      cluster.leave(cluster.selfAddress)
  }

  override def persistenceId: String = "add-actor"
}

object AddShard {

  def create(port: Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("sharding"))
    val system = ActorSystem("addSystem", config)
    startShard(port, system)
  }

  def startSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")
    import system.dispatcher
    implicit val timeout = Timeout(15.seconds)
    val f = system.actorSelection(path) ? Identify(None)
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
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
  def startShard(port: Int, system: ActorSystem): Unit = {
    val path = ActorPath.fromString("akka.tcp://addSystem@127.0.0.1:2551/user/store")
    startSharedJournal(system, port == 2551, path)
    ClusterSharding(system).start(
      typeName = AddActor.shardName,
      entityProps = AddActor.props,
      settings = ClusterShardingSettings(system),
      extractEntityId = AddActor.entityId,
      extractShardId = AddActor.shardId
    )
  }
}
