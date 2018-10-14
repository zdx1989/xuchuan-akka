import CalcActor.Command
import akka.actor.SupervisorStrategy._
import akka.actor._
import akka.pattern._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by zhoudunxiong on 2018/10/14.
  */
class CalcSupervisor extends Actor with ActorLogging {

  val calc = context.actorOf(CalcActor.props, "calc")

  val decider: Decider = {
    case _: ArithmeticException => Resume
  }


  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(5, 5.seconds, false) {
      decider orElse defaultDecider
    }

  override def receive: Receive = {
    case msg @ _ => calc.forward(msg)
  }
}

object CalculatorShard {

  case class CalcCommands(eid: String, msg: Command)

  val shardName = "calcShard"

  val getEntityId: ShardRegion.ExtractEntityId = {
    case CalcCommands(id,msg) => (id,msg)
  }

  val getShardId: ShardRegion.ExtractShardId = {
    case CalcCommands(id,_) => id.head.toString
  }

  def entityProps = Props(new CalcSupervisor)
}

object CalcShards {

  def create(port: Int = 0): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("sharding"))
    val system = ActorSystem("shardingSystem", config)
    startupSharding(port,system)
  }

  def startupSharding(port: Int, system: ActorSystem) = {
    val path = ActorPath.fromString("akka.tcp://shardingSystem@127.0.0.1:2551/user/store")
    startupSharedJournal(system, port == 2551, path)

    ClusterSharding(system).start(
      typeName = CalculatorShard.shardName,
      entityProps = CalculatorShard.entityProps,
      settings = ClusterShardingSettings(system),
      extractEntityId = CalculatorShard.getEntityId,
      extractShardId = CalculatorShard.getShardId
    )
  }

  def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")
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
